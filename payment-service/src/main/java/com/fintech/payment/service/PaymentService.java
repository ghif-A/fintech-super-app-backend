package com.fintech.payment.service;

import com.fintech.payment.dto.MerchantPaymentRequest;
import com.fintech.payment.dto.P2PPaymentRequest;
import com.fintech.payment.dto.PaymentEvent;
import com.fintech.payment.dto.PaymentResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class PaymentService {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String PAYMENT_TOPIC = "payment-events";
    private static final String IDEMPOTENCY_KEY_PREFIX = "idempotency:";
    private static final long IDEMPOTENCY_KEY_EXPIRATION_MINUTES = 10; // Key expires after 10 minutes

    public PaymentResponse processP2PPayment(P2PPaymentRequest request) {
        String idempotencyKey = IDEMPOTENCY_KEY_PREFIX + request.getIdempotencyKey();

        // Check if the request has already been processed
        if (redisTemplate.hasKey(idempotencyKey)) {
            String existingTransactionId = redisTemplate.opsForValue().get(idempotencyKey);
            return new PaymentResponse("PROCESSED", "Payment already processed", existingTransactionId);
        }

        // Generate a unique transaction ID
        String transactionId = UUID.randomUUID().toString();

        // Store idempotency key in Redis with transaction ID and expiration
        redisTemplate.opsForValue().set(idempotencyKey, transactionId, IDEMPOTENCY_KEY_EXPIRATION_MINUTES, TimeUnit.MINUTES);

        // Publish payment event to Kafka
        PaymentEvent paymentEvent = new PaymentEvent(
                "P2P_TRANSFER",
                request.getSenderUserId(),
                request.getReceiverUserId(),
                request.getAmount(),
                transactionId,
                "P2P transfer from " + request.getSenderUserId() + " to " + request.getReceiverUserId()
        );
        kafkaTemplate.send(PAYMENT_TOPIC, transactionId, paymentEvent);

        return new PaymentResponse("PENDING", "P2P payment initiated", transactionId);
    }

    public PaymentResponse processMerchantPayment(MerchantPaymentRequest request) {
        String idempotencyKey = IDEMPOTENCY_KEY_PREFIX + request.getIdempotencyKey();

        // Check if the request has already been processed
        if (redisTemplate.hasKey(idempotencyKey)) {
            String existingTransactionId = redisTemplate.opsForValue().get(idempotencyKey);
            return new PaymentResponse("PROCESSED", "Payment already processed", existingTransactionId);
        }

        // Generate a unique transaction ID
        String transactionId = UUID.randomUUID().toString();

        // Store idempotency key in Redis with transaction ID and expiration
        redisTemplate.opsForValue().set(idempotencyKey, transactionId, IDEMPOTENCY_KEY_EXPIRATION_MINUTES, TimeUnit.MINUTES);

        // Publish payment event to Kafka
        PaymentEvent paymentEvent = new PaymentEvent(
                "MERCHANT_PAYMENT",
                request.getUserId(),
                request.getMerchantId(),
                request.getAmount(),
                transactionId,
                "Merchant payment by " + request.getUserId() + " to merchant " + request.getMerchantId()
        );
        kafkaTemplate.send(PAYMENT_TOPIC, transactionId, paymentEvent);

        return new PaymentResponse("PENDING", "Merchant payment initiated", transactionId);
    }
}
