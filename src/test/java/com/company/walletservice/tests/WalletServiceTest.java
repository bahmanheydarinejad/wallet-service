package com.company.walletservice.tests;

import com.company.walletservice.models.entities.WalletTransaction;
import com.company.walletservice.models.mapper.WalletMapper;
import com.company.walletservice.models.repositories.TransactionRepository;
import com.company.walletservice.models.requests.AddMoneyRequest;
import com.company.walletservice.models.responses.BalanceResponse;
import com.company.walletservice.models.responses.ReferenceResponse;
import com.company.walletservice.services.WalletService;
import com.company.walletservice.services.impl.WalletServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


class WalletServiceTest {

    private final TransactionRepository repository = mock(TransactionRepository.class);
    private final WalletMapper walletMapper = mock(WalletMapper.class);
    private final WalletService service = new WalletServiceImpl(repository, walletMapper);

    @Test
    void testAddMoney_ShouldSaveTransactionAndReturnReferenceId() {
        AddMoneyRequest request = new AddMoneyRequest(BigDecimal.valueOf(500));

        WalletTransaction tx = new WalletTransaction(1L, BigDecimal.valueOf(500));
        tx.setReferenceId(UUID.randomUUID());

        when(repository.save(Mockito.any())).thenReturn(tx);

        ReferenceResponse response = service.addMoney(1L, request);

        assertNotNull(response);
        assertNotNull(response.getReferenceId());
    }

    @Test
    void testGetBalance_ShouldReturnCorrectBalance() {
        Long userId = 1L;
        when(repository.getUserBalance(userId)).thenReturn(BigDecimal.valueOf(1500));

        BalanceResponse response = service.getBalance(userId);

        assertNotNull(response);
        assertEquals(BigDecimal.valueOf(1500), response.getBalance());
    }
}
