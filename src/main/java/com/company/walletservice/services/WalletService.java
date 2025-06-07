package com.company.walletservice.services;

import com.company.walletservice.models.requests.AddMoneyRequest;
import com.company.walletservice.models.responses.BalanceResponse;
import com.company.walletservice.models.responses.ReferenceResponse;

public interface WalletService {

    ReferenceResponse addMoney(Long userId, AddMoneyRequest request);

    BalanceResponse getBalance(Long userId);

}
