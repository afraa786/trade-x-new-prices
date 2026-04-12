package com.alpharedge.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PortfolioSummaryDTO {
    private String portfolioId;
    private String portfolioName;
    private BigDecimal totalValue;
    private BigDecimal totalCostBasis;
    private BigDecimal totalPnlUsd;
    private BigDecimal totalPnlPercent;
    private String bestPerformer;
    private BigDecimal bestPerformerGain;
    private String worstPerformer;
    private BigDecimal worstPerformerLoss;
    private List<HoldingPerformanceDTO> holdings;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class HoldingPerformanceDTO {
        private String holdingId;
        private String coinId;
        private String symbol;
        private BigDecimal quantity;
        private BigDecimal currentPrice;
        private BigDecimal currentValue;
        private BigDecimal costBasis;
        private BigDecimal pnlUsd;
        private BigDecimal pnlPercent;
    }
}
