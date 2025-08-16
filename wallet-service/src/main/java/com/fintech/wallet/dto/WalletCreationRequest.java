package com.fintech.wallet.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class WalletCreationRequest {
    @NotNull
    private Long userId;
}
