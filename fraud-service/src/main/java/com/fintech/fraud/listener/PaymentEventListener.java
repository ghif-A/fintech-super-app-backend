package com.fintech.fraud.listener;

import com.fintech.fraud.dto.PaymentEvent;
import com.fintech.fraud.service.FraudDetectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentEventListener {

    @Autowired
    private FraudDetectionService fraudDetectionService;

    @KafkaListener(topics = "payment-events", groupId = "fraud-group", containerFactory = "kafkaListenerContainerFactory")
    public void listenPaymentEvents(PaymentEvent paymentEvent) {
        fraudDetectionService.detectFraud(paymentEvent);
    }
}
