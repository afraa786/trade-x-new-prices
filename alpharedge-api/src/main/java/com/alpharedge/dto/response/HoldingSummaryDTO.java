package com.alpharedge.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HoldingSummaryDTO {
    private String holdingId;
    private String coinId;
    private String coinName;
    private String symbol;
    private BigDecimal quantity;
    private BigDecimal buyPriceUsd;
    private LocalDate buyDate;
    private String notes;
    private LocalDateTime createdAt;
}
