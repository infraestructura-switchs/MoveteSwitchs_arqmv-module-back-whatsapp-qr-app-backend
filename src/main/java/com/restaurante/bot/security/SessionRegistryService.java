package com.restaurante.bot.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SessionRegistryService {

    private final Map<String, SessionMetadata> activeSessions = new ConcurrentHashMap<>();

    private final long sessionExpirationMs;

    public SessionRegistryService(@Value("${security.session.expiration-ms:3600000}") long sessionExpirationMs) {
        this.sessionExpirationMs = sessionExpirationMs;
    }

    public SessionMetadata registerSession(String sessionId, Long companyId, Long userId) {
        if (sessionId == null) {
            // Defensive: generate session id if caller provided null to avoid NPE on ConcurrentHashMap
            sessionId = UUID.randomUUID().toString().replace("-", "");
        }
        return registerSession(sessionId, companyId, userId, Instant.now().plusMillis(sessionExpirationMs));
    }

    public SessionMetadata registerSession(String sessionId, Long companyId, Long userId, Instant expiresAt) {
        SessionMetadata sessionMetadata = new SessionMetadata(companyId, userId, expiresAt);
        activeSessions.put(sessionId, sessionMetadata);
        return sessionMetadata;
    }

    public boolean isSessionActive(String sessionId) {
        return getSessionStatus(sessionId).active();
    }

    public void invalidateSession(String sessionId) {
        activeSessions.remove(sessionId);
    }

    public SessionStatus getSessionStatus(String sessionId) {
        SessionMetadata sessionMetadata = activeSessions.get(sessionId);
        if (sessionMetadata == null) {
            return new SessionStatus(sessionId, false, true, null, 0L);
        }

        boolean expired = sessionMetadata.expiresAt().isBefore(Instant.now());
        if (expired) {
            activeSessions.remove(sessionId);
            return new SessionStatus(sessionId, false, true, sessionMetadata.expiresAt(), 0L);
        }

        long remainingMs = Math.max(0L, Duration.between(Instant.now(), sessionMetadata.expiresAt()).toMillis());
        return new SessionStatus(sessionId, true, false, sessionMetadata.expiresAt(), remainingMs);
    }

    public long getSessionExpirationMs() {
        return sessionExpirationMs;
    }

    public record SessionMetadata(Long companyId, Long userId, Instant expiresAt) {
    }

    public record SessionStatus(String sessionId, boolean active, boolean expired, Instant expiresAt,
                                long remainingMs) {
    }
}