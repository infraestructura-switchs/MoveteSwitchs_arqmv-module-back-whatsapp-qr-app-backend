package com.restaurante.bot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDeliveryProducts {

    private String phone;
    private Long orderTransactionDeliveryId;
    private List<SellProducts> items;
    private Double total;
}
