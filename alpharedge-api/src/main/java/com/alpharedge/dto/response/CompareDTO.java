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
public class CompareDTO {
    private String coinId;
    private String symbol;
    private String name;
    private BigDecimal priceUsd;
    private BigDecimal priceChange24hPercent;
    private BigDecimal marketCapUsd;
    private BigDecimal volume24hUsd;
    private BigDecimal rsi;
    private String signal;
    private BigDecimal momentumScore;
}
