package com.fintech.wallet.controller;

import com.fintech.wallet.dto.TransactionRequest;
import com.fintech.wallet.dto.TransactionResponse;
import com.fintech.wallet.dto.WalletBalanceResponse;
import com.fintech.wallet.dto.WalletCreationRequest;
import com.fintech.wallet.model.Wallet;
import com.fintech.wallet.service.WalletService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wallets")
public class WalletController {

    @Autowired
    private WalletService walletService;

    @PostMapping
    public ResponseEntity<Wallet> createWallet(@Valid @RequestBody WalletCreationRequest request) {
        Wallet newWallet = walletService.createWallet(request.getUserId());
        return new ResponseEntity<>(newWallet, HttpStatus.CREATED);
    }

    @GetMapping("/{userId}/balance")
    public ResponseEntity<WalletBalanceResponse> getWalletBalance(@PathVariable Long userId) {
        WalletBalanceResponse balance = walletService.getWalletBalance(userId);
        return ResponseEntity.ok(balance);
    }

    @PostMapping("/{userId}/topup")
    public ResponseEntity<TransactionResponse> topUpWallet(@PathVariable Long userId, @Valid @RequestBody TransactionRequest request) {
        TransactionResponse transaction = walletService.topUpWallet(userId, request.getAmount());
        return ResponseEntity.ok(transaction);
    }

    @PostMapping("/{userId}/withdraw")
    public ResponseEntity<TransactionResponse> withdrawFromWallet(@PathVariable Long userId, @Valid @RequestBody TransactionRequest request) {
        TransactionResponse transaction = walletService.withdrawFromWallet(userId, request.getAmount());
        return ResponseEntity.ok(transaction);
    }

    @GetMapping("/{userId}/transactions")
    public ResponseEntity<List<TransactionResponse>> getTransactionHistory(@PathVariable Long userId) {
        List<TransactionResponse> transactions = walletService.getTransactionHistory(userId);
        return ResponseEntity.ok(transactions);
    }
}
