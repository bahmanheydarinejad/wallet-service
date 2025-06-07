package com.company.walletservice.models.requests;

import com.company.walletservice.configurations.NotZero;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddMoneyRequest {

    @NotNull(message = "wallet.amount.required")
    @NotZero(message = "wallet.amount.not.zero")
    private BigDecimal amount;

}
