package com.restaurante.bot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GenerateTokenRequestDTO {

    @NotNull(message = "companyId obligatorio")
    private Long companyId;

    @NotNull(message = "userId obligatorio")
    private Long userId;

    @NotBlank(message = "apiKey obligatorio")
    private String apiKey;

}
