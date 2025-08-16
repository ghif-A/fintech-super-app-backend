package com.fintech.payment.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class MerchantPaymentRequest {
    @NotNull
    private Long userId;

    @NotNull
    private Long merchantId;

    @NotNull
    @Positive
    private BigDecimal amount;

    @NotNull
    private String idempotencyKey;
}
