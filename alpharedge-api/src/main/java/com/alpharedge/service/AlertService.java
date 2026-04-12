package com.alpharedge.service;

import com.alpharedge.document.PriceAlert;
import com.alpharedge.document.PriceSnapshot;
import com.alpharedge.document.TrackedCoin;
import com.alpharedge.dto.request.CreateAlertRequest;
import com.alpharedge.dto.response.AlertDTO;
import com.alpharedge.exception.AlertNotFoundException;
import com.alpharedge.exception.CoinNotFoundException;
import com.alpharedge.exception.UnauthorizedException;
import com.alpharedge.mapper.AlertMapper;
import com.alpharedge.repository.PriceAlertRepository;
import com.alpharedge.repository.PriceSnapshotRepository;
import com.alpharedge.repository.TrackedCoinRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AlertService {

    private final PriceAlertRepository priceAlertRepository;
    private final TrackedCoinRepository trackedCoinRepository;
    private final PriceSnapshotRepository priceSnapshotRepository;
    private final AlertMapper alertMapper;
    private final JavaMailSender mailSender;

    @Autowired
    public AlertService(PriceAlertRepository priceAlertRepository,
                       TrackedCoinRepository trackedCoinRepository,
                       PriceSnapshotRepository priceSnapshotRepository,
                       AlertMapper alertMapper,
                       JavaMailSender mailSender) {
        this.priceAlertRepository = priceAlertRepository;
        this.trackedCoinRepository = trackedCoinRepository;
        this.priceSnapshotRepository = priceSnapshotRepository;
        this.alertMapper = alertMapper;
        this.mailSender = mailSender;
    }

    public AlertDTO createAlert(String userId, CreateAlertRequest request) {
        try {
            log.info("Creating alert for user: {} coin: {}", userId, request.getCoinId());
            TrackedCoin trackedCoin = trackedCoinRepository.findByCoinId(request.getCoinId())
                    .orElseThrow(() -> new CoinNotFoundException("Coin not found: " + request.getCoinId()));

            PriceAlert alert = PriceAlert.builder()
                    .userId(userId)
                    .coinId(request.getCoinId())
                    .coinName(trackedCoin.getName())
                    .symbol(trackedCoin.getSymbol())
                    .alertType(request.getAlertType())
                    .targetPriceUsd(request.getTargetPriceUsd())
                    .notifyEmail(request.getNotifyEmail())
                    .isTriggered(false)
                    .isActive(true)
                    .build();

            alert = priceAlertRepository.save(alert);
            return alertMapper.toDTO(alert);
        } catch (Exception ex) {
            log.error("Error creating alert", ex);
            throw ex;
        }
    }

    public List<AlertDTO> getUserAlerts(String userId) {
        try {
            log.debug("Fetching alerts for user: {}", userId);
            List<PriceAlert> alerts = priceAlertRepository.findByUserIdAndIsActiveTrue(userId);
            return alerts.stream()
                    .map(alertMapper::toDTO)
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            log.error("Error fetching user alerts", ex);
            return new ArrayList<>();
        }
    }

    public void deactivateAlert(String alertId, String userId) {
        try {
            log.info("Deactivating alert: {} for user: {}", alertId, userId);
            PriceAlert alert = priceAlertRepository.findByIdAndUserId(alertId, userId)
                    .orElseThrow(() -> new UnauthorizedException("Alert not found or access denied"));

            alert.setIsActive(false);
            priceAlertRepository.save(alert);
        } catch (Exception ex) {
            log.error("Error deactivating alert", ex);
            throw ex;
        }
    }

    public void checkAndTriggerAlerts() {
        try {
            log.debug("Checking and triggering alerts");
            List<PriceAlert> activeUntriggeredAlerts = priceAlertRepository.findByIsActiveTrueAndIsTriggeredFalse();

            for (PriceAlert alert : activeUntriggeredAlerts) {
                try {
                    PriceSnapshot snapshot = priceSnapshotRepository
                            .findTopByCoinIdOrderByFetchedAtDesc(alert.getCoinId())
                            .orElse(null);

                    if (snapshot != null) {
                        boolean shouldTrigger = false;

                        if (alert.getAlertType() == PriceAlert.AlertType.PRICE_ABOVE) {
                            shouldTrigger = snapshot.getPriceUsd().compareTo(alert.getTargetPriceUsd()) >= 0;
                        } else if (alert.getAlertType() == PriceAlert.AlertType.PRICE_BELOW) {
                            shouldTrigger = snapshot.getPriceUsd().compareTo(alert.getTargetPriceUsd()) <= 0;
                        }

                        if (shouldTrigger) {
                            alert.setIsTriggered(true);
                            alert.setIsActive(false);
                            alert.setTriggeredAt(LocalDateTime.now());
                            priceAlertRepository.save(alert);

                            sendAlertEmail(alert, snapshot.getPriceUsd());
                            log.info("Alert triggered: {} for coin: {}", alert.getId(), alert.getCoinId());
                        }
                    }
                } catch (Exception ex) {
                    log.error("Error processing alert: {}", alert.getId(), ex);
                }
            }
            log.info("Checked {} alerts", activeUntriggeredAlerts.size());
        } catch (Exception ex) {
            log.error("Error in checkAndTriggerAlerts", ex);
        }
    }

    private void sendAlertEmail(PriceAlert alert, BigDecimal currentPrice) {
        try {
            log.debug("Sending alert email to: {}", alert.getNotifyEmail());
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(alert.getNotifyEmail());
            message.setSubject("AlphaEdge Alert — " + alert.getCoinName() + " (" + alert.getSymbol() + ")");
            message.setText(buildAlertEmailBody(alert, currentPrice));

            mailSender.send(message);
            log.info("Alert email sent to: {}", alert.getNotifyEmail());
        } catch (Exception ex) {
            log.error("Error sending alert email to: {}", alert.getNotifyEmail(), ex);
        }
    }

    private String buildAlertEmailBody(PriceAlert alert, BigDecimal currentPrice) {
        return "Your " + alert.getAlertType() + " alert has been triggered.\n\n" +
                "Coin: " + alert.getCoinName() + " (" + alert.getSymbol() + ")\n" +
                "Target Price: $" + alert.getTargetPriceUsd() + "\n" +
                "Current Price: $" + currentPrice + "\n" +
                "Triggered at: " + alert.getTriggeredAt() + "\n\n" +
                "— AlphaEdge";
    }
}
