package com.fintech.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentEvent {
    private String type;
    private Long senderId;
    private Long receiverId;
    private BigDecimal amount;
    private String transactionId;
    private String description;
}
