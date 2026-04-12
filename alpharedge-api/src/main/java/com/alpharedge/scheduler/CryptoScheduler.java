package com.alpharedge.scheduler;

import com.alpharedge.service.AlertService;
import com.alpharedge.service.CoinService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CryptoScheduler {

    private final CoinService coinService;
    private final AlertService alertService;

    @Autowired
    public CryptoScheduler(CoinService coinService, AlertService alertService) {
        this.coinService = coinService;
        this.alertService = alertService;
    }

    @Scheduled(fixedRate = 300000)
    public void priceUpdateJob() {
        try {
            log.info("Starting price update job");
            coinService.fetchAndSaveSnapshots();
            alertService.checkAndTriggerAlerts();
            log.info("Price update complete");
        } catch (Exception ex) {
            log.error("Error in price update job", ex);
        }
    }

    @Scheduled(cron = "0 0 * * * *")
    public void signalComputeJob() {
        try {
            log.info("Starting signal computation job");
            coinService.computeAndSaveSignals();
            log.info("Signal computation complete");
        } catch (Exception ex) {
            log.error("Error in signal computation job", ex);
        }
    }
}
