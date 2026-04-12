package com.alpharedge.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GlobalMarketDTO {
    private BigDecimal totalMarketCapUsd;
    private BigDecimal totalMarketCapInr;
    private BigDecimal totalVolumeUsd;
    private BigDecimal totalVolumeInr;
    private BigDecimal btcMarketCapPercent;
    private BigDecimal ethMarketCapPercent;
    private Integer activeCryptocurrencies;
}
