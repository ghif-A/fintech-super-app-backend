package com.fintech.notification.listener;

import com.fintech.notification.dto.FraudAlert;
import com.fintech.notification.dto.NotificationMessage;
import com.fintech.notification.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class FraudAlertListener {

    @Autowired
    private NotificationService notificationService;

    @KafkaListener(topics = "fraud-alerts", groupId = "notification-group", containerFactory = "kafkaListenerContainerFactory")
    public void listenFraudAlerts(FraudAlert fraudAlert) {
        String message = String.format("Fraud Alert: %s for transaction %s (User: %d)",
                fraudAlert.getRuleTriggered(), fraudAlert.getTransactionId(), fraudAlert.getUserId());

        notificationService.sendFraudAlertToAdmins(
                new NotificationMessage("FRAUD_ALERT", message, fraudAlert));
    }
}
