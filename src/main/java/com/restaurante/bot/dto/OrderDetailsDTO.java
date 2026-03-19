package com.restaurante.bot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class OrderDetailsDTO {

    @NotNull(message = "restaurantTable es obligatorio")
    private Long restaurantTable;

    @NotBlank(message = "phone es obligatorio")
    private String phone;

    private Long companyId;

    @NotEmpty(message = "items no puede ser vacio")
    @Valid
    private List<ItemRequest> items;

    @NotNull(message = "total es obligatorio")
    private Double total;

}
