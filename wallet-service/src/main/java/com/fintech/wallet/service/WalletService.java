package com.fintech.wallet.service;

import com.fintech.wallet.dto.TransactionResponse;
import com.fintech.wallet.dto.WalletBalanceResponse;
import com.fintech.wallet.model.Transaction;
import com.fintech.wallet.model.TransactionType;
import com.fintech.wallet.model.Wallet;
import com.fintech.wallet.repository.TransactionRepository;
import com.fintech.wallet.repository.WalletRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class WalletService {

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    private static final String WALLET_TOPIC = "wallet-events";

    @Transactional
    public Wallet createWallet(Long userId) {
        if (walletRepository.findByUserId(userId).isPresent()) {
            throw new IllegalArgumentException("Wallet already exists for user: " + userId);
        }
        Wallet wallet = new Wallet(userId, BigDecimal.ZERO);
        return walletRepository.save(wallet);
    }

    public WalletBalanceResponse getWalletBalance(Long userId) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found for user: " + userId));
        return new WalletBalanceResponse(wallet.getUserId(), wallet.getBalance());
    }

    @Transactional
    public TransactionResponse topUpWallet(Long userId, BigDecimal amount) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found for user: " + userId));

        wallet.setBalance(wallet.getBalance().add(amount));
        walletRepository.save(wallet);

        Transaction transaction = new Transaction(userId, amount, TransactionType.TOPUP, "Wallet Top-up");
        transactionRepository.save(transaction);

        kafkaTemplate.send(WALLET_TOPIC, "topup", transaction);
        return new TransactionResponse(transaction.getId(), transaction.getUserId(), transaction.getAmount(),
                transaction.getType(), transaction.getDescription(), transaction.getTimestamp());
    }

    @Transactional
    public TransactionResponse withdrawFromWallet(Long userId, BigDecimal amount) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found for user: " + userId));

        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient balance");
        }

        wallet.setBalance(wallet.getBalance().subtract(amount));
        walletRepository.save(wallet);

        Transaction transaction = new Transaction(userId, amount, TransactionType.WITHDRAWAL, "Wallet Withdrawal");
        transactionRepository.save(transaction);

        kafkaTemplate.send(WALLET_TOPIC, "withdrawal", transaction);
        return new TransactionResponse(transaction.getId(), transaction.getUserId(), transaction.getAmount(),
                transaction.getType(), transaction.getDescription(), transaction.getTimestamp());
    }

    public List<TransactionResponse> getTransactionHistory(Long userId) {
        List<Transaction> transactions = transactionRepository.findByUserIdOrderByTimestampDesc(userId);
        return transactions.stream()
                .map(t -> new TransactionResponse(t.getId(), t.getUserId(), t.getAmount(), t.getType(), t.getDescription(), t.getTimestamp()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateWalletBalance(Long userId, BigDecimal amount, TransactionType type, String description) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found for user: " + userId));

        if (type == TransactionType.P2P_RECEIVE || type == TransactionType.REFUND) {
            wallet.setBalance(wallet.getBalance().add(amount));
        } else if (type == TransactionType.P2P_SEND || type == TransactionType.MERCHANT_PAYMENT) {
            if (wallet.getBalance().compareTo(amount) < 0) {
                throw new IllegalArgumentException("Insufficient balance for transaction");
            }
            wallet.setBalance(wallet.getBalance().subtract(amount));
        }
        walletRepository.save(wallet);

        Transaction transaction = new Transaction(userId, amount, type, description);
        transactionRepository.save(transaction);
    }
}
