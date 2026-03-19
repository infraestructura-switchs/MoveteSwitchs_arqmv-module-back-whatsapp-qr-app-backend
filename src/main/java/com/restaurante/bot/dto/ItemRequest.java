package com.restaurante.bot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequest {

    @NotBlank(message = "productId es obligatorio")
    private String productId;

    @NotNull(message = "qty es obligatorio")
    @Min(value = 1, message = "qty debe ser al menos 1")
    private Integer qty;

    private String comment;

    @NotNull(message = "unitPrice es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "unitPrice debe ser mayor que 0")
    private Double unitPrice;

}


