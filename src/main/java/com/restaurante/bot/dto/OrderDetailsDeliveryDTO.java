package com.restaurante.bot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class OrderDetailsDeliveryDTO {

    private Long orderTransactionDeliveryId;
    private String phone;
    private List<SellProducts> items;
    private Double total;
    private String method;
    private String address;
    private Long paymentId;
    private String nameClient;
    private String phoneClient;
    private String mail;
    private Long typeIdentificationId;
    private Long nameIdentification;

}
