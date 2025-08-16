package com.fintech.qr.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GenerateQrResponse {
    private String qrCodeBase64;
    private String payload;
}
