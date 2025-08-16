package com.fintech.kyc.dto;

import lombok.Data;

@Data
public class KycRequest {
    private String userId;
    private String documentType;
    private String documentNumber;
}
