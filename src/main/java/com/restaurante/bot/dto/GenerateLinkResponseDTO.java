package com.restaurante.bot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GenerateLinkResponseDTO {

    @JsonProperty("short_url")
    private String shortUrl;

    @JsonProperty("full_url")
    private String fullUrl;

    @JsonProperty("short_code")
    private String shortCode;

    private String token;

    @JsonProperty("session_id")
    private String sessionId;
}
