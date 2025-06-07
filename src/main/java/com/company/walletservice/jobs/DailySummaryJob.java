package com.company.walletservice.jobs;

import com.company.walletservice.models.repositories.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Slf4j
@RequiredArgsConstructor
public class DailySummaryJob {

    private final TransactionRepository transactionRepository;

    @Scheduled(cron = "0 0 0 * * *") // Every midnight
    public void printTotalTransactions() {
        BigDecimal total = transactionRepository.getTotalTransactionsAmount();
        log.info("Total transaction amount: {}", total);
    }
}
