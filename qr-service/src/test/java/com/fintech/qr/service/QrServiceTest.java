package com.fintech.qr.service;

import com.fintech.qr.dto.GenerateQrRequest;
import com.fintech.qr.dto.QrResponse;
import com.fintech.qr.dto.VerifyQrRequest;
import com.fintech.qr.util.HmacUtil;
import com.google.zxing.WriterException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class QrServiceTest {

    @Mock
    private HmacUtil hmacUtil;

    @InjectMocks
    private QrService qrService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGenerateQr() throws WriterException, IOException {
        GenerateQrRequest request = new GenerateQrRequest();
        request.setMerchantId("merchant123");
        request.setOrderId("order456");
        request.setAmount(100.0);

        when(hmacUtil.sign(anyString())).thenReturn("signed-payload");

        QrResponse response = qrService.generateQr(request);

        assertNotNull(response);
        assertNotNull(response.getQrCode());
        assertTrue(response.isValid());
    }

    @Test
    void testVerifyQr_Success() {
        VerifyQrRequest request = new VerifyQrRequest();
        request.setQrPayload("merchant123:order456:100.00:signed-payload");

        when(hmacUtil.verify("merchant123:order456:100.00", "signed-payload")).thenReturn(true);

        boolean result = qrService.verifyQr(request);

        assertTrue(result);
    }

    @Test
    void testVerifyQr_Failure() {
        VerifyQrRequest request = new VerifyQrRequest();
        request.setQrPayload("merchant123:order456:100.00:invalid-payload");

        when(hmacUtil.verify("merchant123:order456:100.00", "invalid-payload")).thenReturn(false);

        boolean result = qrService.verifyQr(request);

        assertFalse(result);
    }
}
