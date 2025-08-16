package com.fintech.wallet.dto;

import lombok.Data;

@Data
public class DepositRequest {
    private String walletId;
    private Double amount;
}
