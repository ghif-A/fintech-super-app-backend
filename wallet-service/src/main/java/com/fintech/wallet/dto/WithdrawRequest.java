package com.fintech.wallet.dto;

import lombok.Data;

@Data
public class WithdrawRequest {
    private String walletId;
    private Double amount;
}
