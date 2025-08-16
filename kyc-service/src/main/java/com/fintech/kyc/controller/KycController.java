package com.fintech.kyc.controller;

import com.fintech.kyc.dto.KycRequest;
import com.fintech.kyc.dto.KycResponse;
import com.fintech.kyc.service.KycService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/kyc")
public class KycController {

    private final KycService kycService;

    public KycController(KycService kycService) {
        this.kycService = kycService;
    }

    @PostMapping
    public ResponseEntity<KycResponse> submitKyc(@RequestBody KycRequest request) {
        return ResponseEntity.ok(kycService.submitKyc(request));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<KycResponse> getKycStatus(@PathVariable String userId) {
        return ResponseEntity.ok(kycService.getKycStatus(userId));
    }
}
