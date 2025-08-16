package com.fintech.qr.service;

import com.fintech.qr.dto.GenerateQrRequest;
import com.fintech.qr.dto.QrResponse;
import com.fintech.qr.dto.VerifyQrRequest;
import com.fintech.qr.util.HmacUtil;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class QrService {

    private final HmacUtil hmacUtil;

    public QrService(HmacUtil hmacUtil) {
        this.hmacUtil = hmacUtil;
    }

    public QrResponse generateQr(GenerateQrRequest request) throws WriterException, IOException {
        String payload = String.format("%s:%s:%.2f", request.getMerchantId(), request.getOrderId(), request.getAmount());
        String signature = hmacUtil.sign(payload);
        String qrPayload = String.format("%s:%s", payload, signature);

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
        hints.put(EncodeHintType.MARGIN, 1);

        BitMatrix bitMatrix = qrCodeWriter.encode(qrPayload, BarcodeFormat.QR_CODE, 256, 256, hints);

        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        BufferedImage bufferedImage = toBufferedImage(bitMatrix);
        ImageIO.write(bufferedImage, "PNG", pngOutputStream);
        byte[] pngData = pngOutputStream.toByteArray();

        return new QrResponse(Base64.getEncoder().encodeToString(pngData), true);
    }

    public boolean verifyQr(VerifyQrRequest request) {
        String[] parts = request.getQrPayload().split(":");
        if (parts.length != 4) {
            return false;
        }
        String payload = String.format("%s:%s:%s", parts[0], parts[1], parts[2]);
        String signature = parts[3];

        return hmacUtil.verify(payload, signature);
    }

    private BufferedImage toBufferedImage(BitMatrix matrix) {
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, matrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
            }
        }
        return image;
    }
}
