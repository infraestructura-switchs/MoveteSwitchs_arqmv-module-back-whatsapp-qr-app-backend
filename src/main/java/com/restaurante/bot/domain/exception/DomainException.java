package com.restaurante.bot.domain.exception;

public class DomainException extends RuntimeException {
    private final DomainErrorCode code;
    private final String messageKey;
    private final Object[] args;

    public DomainException(DomainErrorCode code, String message) {
        super(message);
        this.code = code;
        this.messageKey = null;
        this.args = new Object[0];
    }

    public DomainException(DomainErrorCode code, String messageKey, Object... args) {
        super(messageKey);
        this.code = code;
        this.messageKey = messageKey;
        this.args = args == null ? new Object[0] : args;
    }

    public DomainErrorCode getCode() {
        return code;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public Object[] getArgs() {
        return args;
    }
}
