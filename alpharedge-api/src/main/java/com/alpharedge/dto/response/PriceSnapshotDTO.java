package com.alpharedge.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PriceSnapshotDTO {
    private String id;
    private String coinId;
    private BigDecimal priceUsd;
    private BigDecimal priceInr;
    private BigDecimal marketCapUsd;
    private BigDecimal volume24hUsd;
    private BigDecimal priceChange24hPercent;
    private BigDecimal priceChange7dPercent;
    private BigDecimal priceChange30dPercent;
    private BigDecimal allTimeHighUsd;
    private BigDecimal allTimeLowUsd;
    private BigDecimal circulatingSupply;
    private BigDecimal totalSupply;
    private Integer marketCapRank;
    private LocalDateTime fetchedAt;
}
