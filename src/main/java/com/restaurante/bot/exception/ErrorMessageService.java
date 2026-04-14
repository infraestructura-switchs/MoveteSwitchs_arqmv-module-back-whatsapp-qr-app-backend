package com.restaurante.bot.exception;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class ErrorMessageService {
    private final MessageSource messageSource;

    public ErrorMessageService(@Autowired(required = false) MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String getMessage(String key, Object... args) {
        if (messageSource == null) return key;
        try {
            return messageSource.getMessage(key, args, Locale.getDefault());
        } catch (Exception e) {
            return key; // fallback to key if not found
        }
    }
}
