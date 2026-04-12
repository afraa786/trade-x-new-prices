package com.alpharedge.dto.response;

import com.alpharedge.document.PriceAlert;
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
public class AlertDTO {
    private String id;
    private String userId;
    private String coinId;
    private String coinName;
    private String symbol;
    private PriceAlert.AlertType alertType;
    private BigDecimal targetPriceUsd;
    private Boolean isTriggered;
    private Boolean isActive;
    private LocalDateTime triggeredAt;
    private String notifyEmail;
    private LocalDateTime createdAt;
}
