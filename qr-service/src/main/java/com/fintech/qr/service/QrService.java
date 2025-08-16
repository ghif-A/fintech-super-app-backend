package com.fintech.qr.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fintech.qr.dto.GenerateQrRequest;
import com.fintech.qr.dto.GenerateQrResponse;
import com.fintech.qr.dto.ScanQrResponse;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class QrService {

    @Value("${qr.hmac.secret}")
    private String hmacSecret;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public GenerateQrResponse generateQrCode(GenerateQrRequest request) throws IOException, WriterException, NoSuchAlgorithmException, InvalidKeyException {
        // 1. Create payload JSON
        Map<String, Object> payloadData = new HashMap<>();
        payloadData.put("merchantId", request.getMerchantId());
        payloadData.put("orderId", request.getOrderId());
        payloadData.put("amount", request.getAmount());
        payloadData.put("currency", request.getCurrency());

        String payloadJson = objectMapper.writeValueAsString(payloadData);

        // 2. Generate HMAC signature
        String signature = generateHmac(payloadJson);

        // 3. Combine payload and signature
        String qrContent = payloadJson + "." + signature;

        // 4. Generate QR code image
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        BitMatrix bitMatrix = qrCodeWriter.encode(qrContent, BarcodeFormat.QR_CODE, 200, 200, hints);

        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
        byte[] pngData = pngOutputStream.toByteArray();

        String qrCodeBase64 = Base64.getEncoder().encodeToString(pngData);

        return new GenerateQrResponse(qrCodeBase64, qrContent);
    }

    public ScanQrResponse scanAndVerifyQrCode(String qrPayload) throws NoSuchAlgorithmException, InvalidKeyException, IOException {
        String[] parts = qrPayload.split("\\.");
        if (parts.length != 2) {
            return new ScanQrResponse(false, "Invalid QR payload format", null);
        }

        String payloadJson = parts[0];
        String receivedSignature = parts[1];

        // 1. Re-generate HMAC signature from payload
        String expectedSignature = generateHmac(payloadJson);

        // 2. Verify signature
        if (!expectedSignature.equals(receivedSignature)) {
            return new ScanQrResponse(false, "QR payload signature mismatch", null);
        }

        // 3. Parse payload data
        Map<String, Object> payloadMap = objectMapper.readValue(payloadJson, Map.class);
        ScanQrResponse.QrPayloadData payloadData = new ScanQrResponse.QrPayloadData(
                ((Number) payloadMap.get("merchantId")).longValue(),
                (String) payloadMap.get("orderId"),
                new java.math.BigDecimal(payloadMap.get("amount").toString()),
                (String) payloadMap.get("currency")
        );

        return new ScanQrResponse(true, "QR payload verified successfully", payloadData);
    }

    private String generateHmac(String data) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(Base64.getDecoder().decode(hmacSecret), "HmacSHA256");
        sha256_HMAC.init(secret_key);
        return Base64.getEncoder().encodeToString(sha256_HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8)));
    }
}