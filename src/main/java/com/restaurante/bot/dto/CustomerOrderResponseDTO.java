package com.restaurante.bot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerOrderResponseDTO {

    private String phone;
    private List<OrderItemDTO> items;
    private Double total;
}
