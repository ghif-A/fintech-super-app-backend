package com.fintech.qr.controller;

import com.fintech.qr.dto.GenerateQrRequest;
import com.fintech.qr.dto.QrResponse;
import com.fintech.qr.dto.VerifyQrRequest;
import com.fintech.qr.service.QrService;
import com.google.zxing.WriterException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/qr")
public class QrController {

    private final QrService qrService;

    public QrController(QrService qrService) {
        this.qrService = qrService;
    }

    @PostMapping("/generate")
    public ResponseEntity<QrResponse> generateQr(@RequestBody GenerateQrRequest request) {
        try {
            return ResponseEntity.ok(qrService.generateQr(request));
        } catch (WriterException | IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<Boolean> verifyQr(@RequestBody VerifyQrRequest request) {
        return ResponseEntity.ok(qrService.verifyQr(request));
    }
}