package com.fintech.payment.controller;

import com.fintech.payment.dto.MerchantPaymentRequest;
import com.fintech.payment.dto.P2PPaymentRequest;
import com.fintech.payment.dto.PaymentResponse;
import com.fintech.payment.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/p2p")
    public ResponseEntity<PaymentResponse> initiateP2PPayment(@Valid @RequestBody P2PPaymentRequest request) {
        PaymentResponse response = paymentService.processP2PPayment(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/merchant")
    public ResponseEntity<PaymentResponse> initiateMerchantPayment(@Valid @RequestBody MerchantPaymentRequest request) {
        PaymentResponse response = paymentService.processMerchantPayment(request);
        return ResponseEntity.ok(response);
    }
}
