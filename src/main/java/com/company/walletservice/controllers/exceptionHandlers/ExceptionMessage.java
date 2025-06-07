package com.company.walletservice.controllers.exceptionHandlers;

public record ExceptionMessage(String clearTextMessage, String code, Object... args) implements
        MessageCodeContainer {

    public ExceptionMessage(String code, Object... args) {
        this(null, code, args);
    }

    @Override
    public String getCode() {
        return code();
    }

}
