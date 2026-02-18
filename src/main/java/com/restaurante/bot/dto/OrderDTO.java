package com.restaurante.bot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {

    private Long orderId;
    private String productId;
    private String name;
    private int qty;
    private Double unitePrice;
    private Double totalPrice;
    private String date;

}
