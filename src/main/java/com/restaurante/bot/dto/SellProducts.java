package com.restaurante.bot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SellProducts {

    private Long productId;
    private String productName;
    private Long qty;
    private Double unitePrice;
    private String comment;
}
