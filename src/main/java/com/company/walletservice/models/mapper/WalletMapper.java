package com.company.walletservice.models.mapper;

import com.company.walletservice.models.entities.WalletTransaction;
import com.company.walletservice.models.requests.AddMoneyRequest;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface WalletMapper {

    WalletTransaction toWalletTransaction(Long userId, AddMoneyRequest request);

}
