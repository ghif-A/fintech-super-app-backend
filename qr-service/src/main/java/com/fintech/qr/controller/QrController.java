package com.fintech.qr.controller;

import com.fintech.qr.dto.GenerateQrRequest;
import com.fintech.qr.dto.GenerateQrResponse;
import com.fintech.qr.dto.ScanQrRequest;
import com.fintech.qr.dto.ScanQrResponse;
import com.fintech.qr.service.QrService;
import com.google.zxing.WriterException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping("/api/qr")
public class QrController {

    @Autowired
    private QrService qrService;

    @PostMapping("/generate")
    public ResponseEntity<GenerateQrResponse> generateQrCode(@Valid @RequestBody GenerateQrRequest request) throws IOException, WriterException, NoSuchAlgorithmException, InvalidKeyException {
        GenerateQrResponse response = qrService.generateQrCode(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/scan")
    public ResponseEntity<ScanQrResponse> scanQrCode(@Valid @RequestBody ScanQrRequest request) throws NoSuchAlgorithmException, InvalidKeyException, IOException {
        ScanQrResponse response = qrService.scanAndVerifyQrCode(request.getQrPayload());
        return ResponseEntity.ok(response);
    }
}
