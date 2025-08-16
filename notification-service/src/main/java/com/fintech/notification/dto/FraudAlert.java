package com.fintech.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FraudAlert {
    private String alertId;
    private String transactionId;
    private Long userId;
    private String ruleTriggered;
    private String description;
    private LocalDateTime timestamp;
}
