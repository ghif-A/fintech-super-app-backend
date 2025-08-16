package com.fintech.wallet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class WalletBalanceResponse {
    private Long userId;
    private BigDecimal balance;
}
