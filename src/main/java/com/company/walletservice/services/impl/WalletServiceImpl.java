package com.company.walletservice.services.impl;

import com.company.walletservice.models.mapper.WalletMapper;
import com.company.walletservice.models.repositories.TransactionRepository;
import com.company.walletservice.models.requests.AddMoneyRequest;
import com.company.walletservice.models.responses.BalanceResponse;
import com.company.walletservice.models.responses.ReferenceResponse;
import com.company.walletservice.services.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final TransactionRepository transactionRepository;
    private final WalletMapper walletMapper;

    @Transactional
    public ReferenceResponse addMoney(Long userId, AddMoneyRequest request) {
        return new ReferenceResponse(transactionRepository.save(walletMapper.toWalletTransaction(userId, request)).getReferenceId().toString());
    }

    public BalanceResponse getBalance(Long userId) {
        return new BalanceResponse(transactionRepository.getUserBalance(userId));
    }

}
