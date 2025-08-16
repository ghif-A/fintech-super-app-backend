package com.fintech.payment.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class P2PPaymentRequest {
    @NotNull
    private Long senderUserId;

    @NotNull
    private Long receiverUserId;

    @NotNull
    @Positive
    private BigDecimal amount;

    @NotNull
    private String idempotencyKey;
}
