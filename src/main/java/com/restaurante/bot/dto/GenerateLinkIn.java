package com.restaurante.bot.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenerateLinkIn {

    @NotNull(message = "The companyId is required.")
    private Long companyId;
    @NotBlank(message = "The apiKey is required.")
    private String apiKey;

    private Long userId;

    @NotBlank(message = "The sessionId is required.")
    private String sessionId;

    @NotBlank(message = "The mesa is required.")
    private String mesa;
    private String qr;
    private String delivery;

}
