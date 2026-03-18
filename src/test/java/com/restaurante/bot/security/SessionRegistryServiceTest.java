package com.restaurante.bot.security;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SessionRegistryServiceTest {

    private final SessionRegistryService sessionRegistryService = new SessionRegistryService(1000L);

    @Test
    void shouldRegisterAndInvalidateSession() {
        sessionRegistryService.registerSession("session-1", 273L, 9L);

        assertTrue(sessionRegistryService.isSessionActive("session-1"));

        sessionRegistryService.invalidateSession("session-1");

        assertFalse(sessionRegistryService.isSessionActive("session-1"));
    }

    @Test
    void shouldExpireSessionBasedOnConfiguredTtl() throws Exception {
        SessionRegistryService shortLivedSessionRegistry = new SessionRegistryService(5L);
        shortLivedSessionRegistry.registerSession("session-2", 273L, 9L);

        Thread.sleep(20L);

        SessionRegistryService.SessionStatus sessionStatus = shortLivedSessionRegistry.getSessionStatus("session-2");
        assertFalse(sessionStatus.active());
        assertTrue(sessionStatus.expired());
        assertFalse(sessionStatus.remainingMs() > 0L);
    }
}