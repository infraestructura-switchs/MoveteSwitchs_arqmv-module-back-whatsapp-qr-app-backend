package com.restaurante.bot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class OrderDetailsDTO {

    private Long restaurantTable;
    private String phone;
    private Long companyId;
    private List<ItemRequest> items;
    private Double total;

}
