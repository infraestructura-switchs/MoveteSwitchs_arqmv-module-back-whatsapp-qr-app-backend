package com.restaurante.bot.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class CustomErrorException extends RuntimeException {
    private HttpStatus status = null;

    private Object data = null;

    /** Optional message key to resolve a friendly message for clients via ErrorMessageService */
    private String messageKey = null;

    public CustomErrorException() {
        super();
    }

    public CustomErrorException(String message) {
        super(message);
    }

    public CustomErrorException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public CustomErrorException(HttpStatus status, String message, Object data) {
        this(status, message);
        this.data = data;
    }

    public CustomErrorException(HttpStatus status, String messageKey, Object data, boolean isMessageKey) {
        super(messageKey);
        this.status = status;
        if (isMessageKey) this.messageKey = messageKey;
        this.data = data;
    }

    public CustomErrorException(HttpStatus status, String messageKey, boolean isMessageKey) {
        this(status, messageKey, null, isMessageKey);
    }
}