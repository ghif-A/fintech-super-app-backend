package com.fintech.wallet.service;

import com.fintech.wallet.dto.*;
import com.fintech.wallet.model.ETransactionType;
import com.fintech.wallet.model.Transaction;
import com.fintech.wallet.model.Wallet;
import com.fintech.wallet.repository.TransactionRepository;
import com.fintech.wallet.repository.WalletRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WalletService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public WalletService(WalletRepository walletRepository, TransactionRepository transactionRepository, KafkaTemplate<String, Object> kafkaTemplate) {
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Transactional
    public WalletResponse createWallet(CreateWalletRequest request) {
        Wallet wallet = new Wallet();
        wallet.setUserId(request.getUserId());
        wallet.setBalance(0.0);
        wallet = walletRepository.save(wallet);
        return new WalletResponse(wallet.getId(), wallet.getUserId(), wallet.getBalance());
    }

    public WalletResponse getWalletByUserId(String userId) {
        Wallet wallet = walletRepository.findByUserId(userId).orElseThrow(() -> new RuntimeException("Wallet not found"));
        return new WalletResponse(wallet.getId(), wallet.getUserId(), wallet.getBalance());
    }

    @Transactional
    public WalletResponse deposit(DepositRequest request) {
        Wallet wallet = walletRepository.findById(request.getWalletId()).orElseThrow(() -> new RuntimeException("Wallet not found"));
        wallet.setBalance(wallet.getBalance() + request.getAmount());
        wallet = walletRepository.save(wallet);

        Transaction transaction = new Transaction();
        transaction.setWalletId(wallet.getId());
        transaction.setAmount(request.getAmount());
        transaction.setType(ETransactionType.DEPOSIT);
        transaction.setTimestamp(LocalDateTime.now());
        transactionRepository.save(transaction);

        kafkaTemplate.send("wallet-events", transaction);

        return new WalletResponse(wallet.getId(), wallet.getUserId(), wallet.getBalance());
    }

    @Transactional
    public WalletResponse withdraw(WithdrawRequest request) {
        Wallet wallet = walletRepository.findById(request.getWalletId()).orElseThrow(() -> new RuntimeException("Wallet not found"));
        if (wallet.getBalance() < request.getAmount()) {
            throw new RuntimeException("Insufficient funds");
        }
        wallet.setBalance(wallet.getBalance() - request.getAmount());
        wallet = walletRepository.save(wallet);

        Transaction transaction = new Transaction();
        transaction.setWalletId(wallet.getId());
        transaction.setAmount(request.getAmount());
        transaction.setType(ETransactionType.WITHDRAWAL);
        transaction.setTimestamp(LocalDateTime.now());
        transactionRepository.save(transaction);

        kafkaTemplate.send("wallet-events", transaction);

        return new WalletResponse(wallet.getId(), wallet.getUserId(), wallet.getBalance());
    }

    public List<TransactionResponse> getTransactionsByWalletId(String walletId) {
        List<Transaction> transactions = transactionRepository.findByWalletId(walletId);
        return transactions.stream()
                .map(t -> new TransactionResponse(t.getId(), t.getWalletId(), t.getAmount(), t.getType().toString(), t.getTimestamp()))
                .collect(Collectors.toList());
    }
}