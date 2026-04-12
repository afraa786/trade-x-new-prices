package com.alpharedge.service;

import com.alpharedge.document.Holding;
import com.alpharedge.document.Portfolio;
import com.alpharedge.document.PriceSnapshot;
import com.alpharedge.document.TrackedCoin;
import com.alpharedge.dto.request.AddHoldingRequest;
import com.alpharedge.dto.request.CreatePortfolioRequest;
import com.alpharedge.dto.response.PortfolioDTO;
import com.alpharedge.dto.response.PortfolioSummaryDTO;
import com.alpharedge.exception.CoinNotFoundException;
import com.alpharedge.exception.PortfolioNotFoundException;
import com.alpharedge.exception.UnauthorizedException;
import com.alpharedge.mapper.PortfolioMapper;
import com.alpharedge.repository.PortfolioRepository;
import com.alpharedge.repository.PriceSnapshotRepository;
import com.alpharedge.repository.TrackedCoinRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final TrackedCoinRepository trackedCoinRepository;
    private final PriceSnapshotRepository priceSnapshotRepository;
    private final PortfolioMapper portfolioMapper;

    @Autowired
    public PortfolioService(PortfolioRepository portfolioRepository,
                           TrackedCoinRepository trackedCoinRepository,
                           PriceSnapshotRepository priceSnapshotRepository,
                           PortfolioMapper portfolioMapper) {
        this.portfolioRepository = portfolioRepository;
        this.trackedCoinRepository = trackedCoinRepository;
        this.priceSnapshotRepository = priceSnapshotRepository;
        this.portfolioMapper = portfolioMapper;
    }

    public PortfolioDTO createPortfolio(String userId, CreatePortfolioRequest request) {
        try {
            log.info("Creating portfolio for user: {}", userId);
            Portfolio portfolio = Portfolio.builder()
                    .userId(userId)
                    .name(request.getName())
                    .holdings(new ArrayList<>())
                    .build();

            portfolio = portfolioRepository.save(portfolio);
            return portfolioMapper.toDTO(portfolio);
        } catch (Exception ex) {
            log.error("Error creating portfolio", ex);
            throw ex;
        }
    }

    public List<PortfolioDTO> getPortfolios(String userId) {
        try {
            log.debug("Fetching portfolios for user: {}", userId);
            List<Portfolio> portfolios = portfolioRepository.findByUserId(userId);
            return portfolios.stream()
                    .map(portfolioMapper::toDTO)
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            log.error("Error fetching portfolios", ex);
            return new ArrayList<>();
        }
    }

    public PortfolioDTO addHolding(String portfolioId, String userId, AddHoldingRequest request) {
        try {
            log.info("Adding holding to portfolio: {} for user: {}", portfolioId, userId);
            Portfolio portfolio = portfolioRepository.findByIdAndUserId(portfolioId, userId)
                    .orElseThrow(() -> new UnauthorizedException("Portfolio not found or access denied"));

            TrackedCoin trackedCoin = trackedCoinRepository.findByCoinId(request.getCoinId())
                    .orElseThrow(() -> new CoinNotFoundException("Coin not found: " + request.getCoinId()));

            Holding holding = Holding.builder()
                    .coinId(request.getCoinId())
                    .coinName(trackedCoin.getName())
                    .symbol(trackedCoin.getSymbol())
                    .quantity(request.getQuantity())
                    .buyPriceUsd(request.getBuyPriceUsd())
                    .buyDate(request.getBuyDate())
                    .notes(request.getNotes())
                    .build();

            portfolio.getHoldings().add(holding);
            portfolio = portfolioRepository.save(portfolio);

            return portfolioMapper.toDTO(portfolio);
        } catch (Exception ex) {
            log.error("Error adding holding to portfolio", ex);
            throw ex;
        }
    }

    public PortfolioDTO removeHolding(String portfolioId, String holdingId, String userId) {
        try {
            log.info("Removing holding from portfolio: {} for user: {}", portfolioId, userId);
            Portfolio portfolio = portfolioRepository.findByIdAndUserId(portfolioId, userId)
                    .orElseThrow(() -> new UnauthorizedException("Portfolio not found or access denied"));

            portfolio.setHoldings(portfolio.getHoldings().stream()
                    .filter(h -> !h.getHoldingId().equals(holdingId))
                    .collect(Collectors.toList()));

            portfolio = portfolioRepository.save(portfolio);
            return portfolioMapper.toDTO(portfolio);
        } catch (Exception ex) {
            log.error("Error removing holding from portfolio", ex);
            throw ex;
        }
    }

    public PortfolioSummaryDTO getPortfolioSummary(String portfolioId, String userId) {
        try {
            log.debug("Fetching portfolio summary: {} for user: {}", portfolioId, userId);
            Portfolio portfolio = portfolioRepository.findByIdAndUserId(portfolioId, userId)
                    .orElseThrow(() -> new UnauthorizedException("Portfolio not found or access denied"));

            BigDecimal totalValue = BigDecimal.ZERO;
            BigDecimal totalCostBasis = BigDecimal.ZERO;
            String bestPerformer = null;
            BigDecimal bestGain = BigDecimal.valueOf(Double.NEGATIVE_INFINITY);
            String worstPerformer = null;
            BigDecimal worstGain = BigDecimal.valueOf(Double.POSITIVE_INFINITY);

            List<PortfolioSummaryDTO.HoldingPerformanceDTO> performances = new ArrayList<>();

            for (Holding holding : portfolio.getHoldings()) {
                PriceSnapshot snapshot = priceSnapshotRepository
                        .findTopByCoinIdOrderByFetchedAtDesc(holding.getCoinId())
                        .orElse(null);

                if (snapshot != null) {
                    BigDecimal currentPrice = snapshot.getPriceUsd();
                    BigDecimal currentValue = holding.getQuantity().multiply(currentPrice);
                    BigDecimal costBasis = holding.getQuantity().multiply(holding.getBuyPriceUsd());
                    BigDecimal pnlUsd = currentValue.subtract(costBasis);
                    BigDecimal pnlPercent = costBasis.compareTo(BigDecimal.ZERO) != 0 ?
                            pnlUsd.multiply(new BigDecimal(100)).divide(costBasis, 8, java.math.RoundingMode.HALF_UP) :
                            BigDecimal.ZERO;

                    totalValue = totalValue.add(currentValue);
                    totalCostBasis = totalCostBasis.add(costBasis);

                    if (pnlPercent.compareTo(bestGain) > 0) {
                        bestGain = pnlPercent;
                        bestPerformer = holding.getSymbol();
                    }

                    if (pnlPercent.compareTo(worstGain) < 0) {
                        worstGain = pnlPercent;
                        worstPerformer = holding.getSymbol();
                    }

                    performances.add(PortfolioSummaryDTO.HoldingPerformanceDTO.builder()
                            .holdingId(holding.getHoldingId())
                            .coinId(holding.getCoinId())
                            .symbol(holding.getSymbol())
                            .quantity(holding.getQuantity())
                            .currentPrice(currentPrice)
                            .currentValue(currentValue)
                            .costBasis(costBasis)
                            .pnlUsd(pnlUsd)
                            .pnlPercent(pnlPercent)
                            .build());
                }
            }

            BigDecimal totalPnlUsd = totalValue.subtract(totalCostBasis);
            BigDecimal totalPnlPercent = totalCostBasis.compareTo(BigDecimal.ZERO) != 0 ?
                    totalPnlUsd.multiply(new BigDecimal(100)).divide(totalCostBasis, 8, java.math.RoundingMode.HALF_UP) :
                    BigDecimal.ZERO;

            return PortfolioSummaryDTO.builder()
                    .portfolioId(portfolioId)
                    .portfolioName(portfolio.getName())
                    .totalValue(totalValue)
                    .totalCostBasis(totalCostBasis)
                    .totalPnlUsd(totalPnlUsd)
                    .totalPnlPercent(totalPnlPercent)
                    .bestPerformer(bestPerformer)
                    .bestPerformerGain(bestGain.compareTo(BigDecimal.valueOf(Double.NEGATIVE_INFINITY)) == 0 ? BigDecimal.ZERO : bestGain)
                    .worstPerformer(worstPerformer)
                    .worstPerformerLoss(worstGain.compareTo(BigDecimal.valueOf(Double.POSITIVE_INFINITY)) == 0 ? BigDecimal.ZERO : worstGain)
                    .holdings(performances)
                    .build();
        } catch (Exception ex) {
            log.error("Error fetching portfolio summary", ex);
            throw ex;
        }
    }
}
