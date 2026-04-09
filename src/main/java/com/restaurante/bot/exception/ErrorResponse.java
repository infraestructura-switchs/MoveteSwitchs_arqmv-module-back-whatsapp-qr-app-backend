package com.restaurante.bot.exception;

import java.time.LocalDateTime;

public class ErrorResponse {
    private LocalDateTime timestamp;
    private String code;
    private String message;
    private String traceId;

    public ErrorResponse() { }

    public ErrorResponse(LocalDateTime timestamp, String code, String message, String traceId) {
        this.timestamp = timestamp;
        this.code = code;
        this.message = message;
        this.traceId = traceId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }
}
