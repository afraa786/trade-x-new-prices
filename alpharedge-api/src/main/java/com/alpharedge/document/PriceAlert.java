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
@Document(collection = "price_alerts")
public class PriceAlert {
    @Id
    private String id;

    private String userId;
    private String coinId;
    private String coinName;
    private String symbol;
    private AlertType alertType;
    private BigDecimal targetPriceUsd;

    @Builder.Default
    private Boolean isTriggered = false;

    @Builder.Default
    private Boolean isActive = true;

    private LocalDateTime triggeredAt;
    private String notifyEmail;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum AlertType {
        PRICE_ABOVE,
        PRICE_BELOW
    }
}
