package com.fintech.qr.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class GenerateQrRequest {
    @NotNull
    private Long merchantId;

    @NotNull
    private String orderId;

    @NotNull
    @Positive
    private BigDecimal amount;

    private String currency = "USD";
}
