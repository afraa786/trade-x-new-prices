package com.alpharedge.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "price_snapshots")
public class PriceSnapshot {
    @Id
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

    @Builder.Default
    private LocalDateTime fetchedAt = LocalDateTime.now();
}
