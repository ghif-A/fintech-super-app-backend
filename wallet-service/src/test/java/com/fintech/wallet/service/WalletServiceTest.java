package com.fintech.wallet.service;

import com.fintech.wallet.dto.*;
import com.fintech.wallet.model.Wallet;
import com.fintech.wallet.repository.TransactionRepository;
import com.fintech.wallet.repository.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class WalletServiceTest {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private WalletService walletService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateWallet() {
        CreateWalletRequest request = new CreateWalletRequest();
        request.setUserId("user123");

        Wallet wallet = new Wallet("wallet456", "user123", 0.0);

        when(walletRepository.save(any(Wallet.class))).thenReturn(wallet);

        WalletResponse response = walletService.createWallet(request);

        assertNotNull(response);
        assertEquals("wallet456", response.getId());
        assertEquals("user123", response.getUserId());
        assertEquals(0.0, response.getBalance());
    }

    @Test
    void testGetWalletByUserId() {
        Wallet wallet = new Wallet("wallet456", "user123", 100.0);

        when(walletRepository.findByUserId("user123")).thenReturn(Optional.of(wallet));

        WalletResponse response = walletService.getWalletByUserId("user123");

        assertNotNull(response);
        assertEquals("wallet456", response.getId());
        assertEquals("user123", response.getUserId());
        assertEquals(100.0, response.getBalance());
    }

    @Test
    void testDeposit() {
        DepositRequest request = new DepositRequest();
        request.setWalletId("wallet456");
        request.setAmount(50.0);

        Wallet wallet = new Wallet("wallet456", "user123", 100.0);

        when(walletRepository.findById("wallet456")).thenReturn(Optional.of(wallet));
        when(walletRepository.save(any(Wallet.class))).thenReturn(wallet);

        WalletResponse response = walletService.deposit(request);

        assertNotNull(response);
        assertEquals(150.0, response.getBalance());
    }

    @Test
    void testWithdraw_Success() {
        WithdrawRequest request = new WithdrawRequest();
        request.setWalletId("wallet456");
        request.setAmount(50.0);

        Wallet wallet = new Wallet("wallet456", "user123", 100.0);

        when(walletRepository.findById("wallet456")).thenReturn(Optional.of(wallet));
        when(walletRepository.save(any(Wallet.class))).thenReturn(wallet);

        WalletResponse response = walletService.withdraw(request);

        assertNotNull(response);
        assertEquals(50.0, response.getBalance());
    }

    @Test
    void testWithdraw_InsufficientFunds() {
        WithdrawRequest request = new WithdrawRequest();
        request.setWalletId("wallet456");
        request.setAmount(150.0);

        Wallet wallet = new Wallet("wallet456", "user123", 100.0);

        when(walletRepository.findById("wallet456")).thenReturn(Optional.of(wallet));

        assertThrows(RuntimeException.class, () -> walletService.withdraw(request));
    }
}
