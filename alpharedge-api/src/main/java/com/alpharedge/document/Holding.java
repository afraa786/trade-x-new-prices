package com.alpharedge.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Holding {
    @Builder.Default
    private String holdingId = UUID.randomUUID().toString();

    private String coinId;
    private String coinName;
    private String symbol;
    private BigDecimal quantity;
    private BigDecimal buyPriceUsd;
    private LocalDate buyDate;
    private String notes;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
