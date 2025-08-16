package com.fintech.notification.listener;

import com.fintech.notification.dto.NotificationMessage;
import com.fintech.notification.dto.PaymentEvent;
import com.fintech.notification.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentNotificationListener {

    @Autowired
    private NotificationService notificationService;

    @KafkaListener(topics = "payment-events", groupId = "notification-group", containerFactory = "kafkaListenerContainerFactory")
    public void listenPaymentEvents(PaymentEvent paymentEvent) {
        String message = String.format("New %s transaction: %.2f from user %d to user/merchant %d (ID: %s)",
                paymentEvent.getType(), paymentEvent.getAmount(), paymentEvent.getSenderId(), paymentEvent.getReceiverId(), paymentEvent.getTransactionId());

        // Notify sender
        if (paymentEvent.getSenderId() != null) {
            notificationService.sendNotificationToUser(paymentEvent.getSenderId(),
                    new NotificationMessage("PAYMENT_UPDATE", message, paymentEvent));
        }

        // Notify receiver
        if (paymentEvent.getReceiverId() != null) {
            notificationService.sendNotificationToUser(paymentEvent.getReceiverId(),
                    new NotificationMessage("PAYMENT_UPDATE", message, paymentEvent));
        }
    }
}
