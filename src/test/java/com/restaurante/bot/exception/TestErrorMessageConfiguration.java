package com.restaurante.bot.exception;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestErrorMessageConfiguration {

    @Bean
    public ErrorMessageService errorMessageService() {
        // return the production implementation with no MessageSource (fallback to keys)
        return new ErrorMessageService(null);
    }
}
