package com.company.walletservice.controllers.exceptionHandlers;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public record GeneralResponse(List<? extends MessageCodeContainer> messages) {

    public GeneralResponse(MessageCodeContainer... messages) {
        this(Optional.ofNullable(messages).map(Arrays::asList).orElse(List.of()));
    }

}
