package com.company.walletservice.controllers.rest;

import com.company.walletservice.models.requests.AddMoneyRequest;
import com.company.walletservice.models.responses.BalanceResponse;
import com.company.walletservice.models.responses.ReferenceResponse;
import com.company.walletservice.services.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wallet")
@RequiredArgsConstructor
public class WalletRestController {

    private final WalletService walletService;

    @GetMapping("/{userId}/balance")
    public BalanceResponse getBalance(@PathVariable Long userId) {
        return walletService.getBalance(userId);
    }

    @PutMapping("/{userId}/add-money")
    public ReferenceResponse addMoney(@PathVariable Long userId, @Validated @RequestBody AddMoneyRequest req) {
        return walletService.addMoney(userId, req);
    }

}
