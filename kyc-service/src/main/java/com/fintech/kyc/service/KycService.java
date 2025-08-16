package com.fintech.kyc.service;

import com.fintech.kyc.dto.KycRequest;
import com.fintech.kyc.dto.KycResponse;
import com.fintech.kyc.model.EKycStatus;
import com.fintech.kyc.model.Kyc;
import com.fintech.kyc.repository.KycRepository;
import org.springframework.stereotype.Service;

@Service
public class KycService {

    private final KycRepository kycRepository;

    public KycService(KycRepository kycRepository) {
        this.kycRepository = kycRepository;
    }

    public KycResponse submitKyc(KycRequest request) {
        Kyc kyc = new Kyc();
        kyc.setUserId(request.getUserId());
        kyc.setDocumentType(request.getDocumentType());
        kyc.setDocumentNumber(request.getDocumentNumber());

        // Simulate KYC validation
        if (request.getDocumentNumber().endsWith("0")) {
            kyc.setStatus(EKycStatus.REJECTED);
        } else {
            kyc.setStatus(EKycStatus.APPROVED);
        }

        kyc = kycRepository.save(kyc);

        return new KycResponse(kyc.getId(), kyc.getUserId(), kyc.getStatus().toString());
    }

    public KycResponse getKycStatus(String userId) {
        Kyc kyc = kycRepository.findByUserId(userId).orElseThrow(() -> new RuntimeException("KYC not found"));
        return new KycResponse(kyc.getId(), kyc.getUserId(), kyc.getStatus().toString());
    }
}
