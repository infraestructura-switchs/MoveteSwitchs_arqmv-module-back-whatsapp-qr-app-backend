package com.restaurante.bot.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RestaurantTable {
    private Integer tableId;
    private Long tableNumber;
    private Long status;
    private Long companyId;
}
