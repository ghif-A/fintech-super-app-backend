package com.fintech.wallet.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "wallets")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Wallet {

    @Id
    private Long userId; // User ID from Auth Service

    @Column(nullable = false)
    private BigDecimal balance;

    @Version
    private Long version; // For optimistic locking

    public Wallet(Long userId, BigDecimal balance) {
        this.userId = userId;
        this.balance = balance;
        this.version = 0L;
    }
}
