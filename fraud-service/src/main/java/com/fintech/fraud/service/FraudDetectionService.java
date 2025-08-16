package com.fintech.fraud.service;

import com.fintech.fraud.dto.FraudAlert;
import com.fintech.fraud.dto.PaymentEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class FraudDetectionService {

    private static final Logger logger = LoggerFactory.getLogger(FraudDetectionService.class);
    private static final String FRAUD_ALERT_TOPIC = "fraud-alerts";

    @Value("${fraud.threshold.daily-amount}")
    private BigDecimal dailyAmountThreshold;

    @Value("${fraud.threshold.transaction-count}")
    private int transactionCountThreshold;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    public void detectFraud(PaymentEvent paymentEvent) {
        logger.info("Processing payment event for fraud detection: {}", paymentEvent);

        // Rule 1: High single transaction amount
        if (paymentEvent.getAmount().compareTo(new BigDecimal("500.00")) > 0) {
            sendFraudAlert(paymentEvent, "High single transaction amount");
            return;
        }

        // Rule 2: Suspiciously large amount for P2P transfer
        if ("P2P_TRANSFER".equals(paymentEvent.getType()) && paymentEvent.getAmount().compareTo(new BigDecimal("200.00")) > 0) {
            sendFraudAlert(paymentEvent, "Suspiciously large P2P transfer");
            return;
        }

        // Rule 3: Rapid multiple transactions (simulated - in a real system, this would involve stateful tracking)
        // For demonstration, we'll just log a potential alert for any transaction for now.
        // In a real system, this would involve checking recent transaction history for the user.
        if (Math.random() < 0.01) { // 1% chance to trigger a simulated rapid transaction alert
            sendFraudAlert(paymentEvent, "Simulated rapid multiple transactions");
            return;
        }

        logger.info("No fraud detected for payment event: {}", paymentEvent.getTransactionId());
    }

    private void sendFraudAlert(PaymentEvent paymentEvent, String ruleTriggered) {
        FraudAlert fraudAlert = new FraudAlert(
                UUID.randomUUID().toString(),
                paymentEvent.getTransactionId(),
                paymentEvent.getSenderId() != null ? paymentEvent.getSenderId() : paymentEvent.getReceiverId(),
                ruleTriggered,
                "Potential fraud detected for transaction " + paymentEvent.getTransactionId() + ": " + ruleTriggered,
                LocalDateTime.now()
        );
        logger.warn("FRAUD ALERT: {}", fraudAlert);
        kafkaTemplate.send(FRAUD_ALERT_TOPIC, fraudAlert.getAlertId(), fraudAlert);
    }
}
