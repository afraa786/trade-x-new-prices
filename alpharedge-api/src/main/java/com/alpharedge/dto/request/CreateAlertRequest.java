package com.alpharedge.dto.request;

import com.alpharedge.document.PriceAlert;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateAlertRequest {
    @NotBlank(message = "Coin ID is required")
    private String coinId;

    @NotNull(message = "Alert type is required")
    private PriceAlert.AlertType alertType;

    @NotNull(message = "Target price is required")
    @DecimalMin(value = "0.01", message = "Target price must be at least 0.01")
    private BigDecimal targetPriceUsd;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String notifyEmail;
}
