package com.restaurante.bot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SessionValidationResponseDTO {

    @JsonProperty("session_id")
    private String sessionId;

    private boolean active;

    private boolean expired;

    @JsonProperty("remaining_ms")
    private long remainingMs;

    @JsonProperty("expires_at")
    private Instant expiresAt;
}