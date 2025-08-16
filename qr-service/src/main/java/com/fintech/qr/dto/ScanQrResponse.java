package com.fintech.qr.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ScanQrResponse {
    private boolean isValid;
    private String message;
    private QrPayloadData payloadData;

    @Data
    @AllArgsConstructor
    public static class QrPayloadData {
        private Long merchantId;
        private String orderId;
        private java.math.BigDecimal amount;
        private String currency;
    }
}
