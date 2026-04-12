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
public class CoinDetailDTO {
    private String id;
    private String coinId;
    private String symbol;
    private String name;
    private Boolean isActive;
    private PriceSnapshotDTO priceSnapshot;
    private CoinSignalDTO signal;
    private LocalDateTime createdAt;
}
