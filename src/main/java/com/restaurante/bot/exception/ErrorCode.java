package com.restaurante.bot.exception;

public enum ErrorCode {
    INVALID_REQUEST("INVALID_REQUEST"),
    NOT_FOUND("NOT_FOUND"),
    UNAUTHORIZED("UNAUTHORIZED"),
    FORBIDDEN("FORBIDDEN"),
    CONFLICT("CONFLICT"),
    SERVICE_UNAVAILABLE("SERVICE_UNAVAILABLE"),
    INTERNAL_ERROR("INTERNAL_ERROR");

    private final String code;

    ErrorCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return this.code;
    }
}
