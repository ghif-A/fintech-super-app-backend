package com.fintech.qr.dto;

import lombok.Data;

@Data
public class GenerateQrRequest {
    private String merchantId;
    private String orderId;
    private Double amount;
}