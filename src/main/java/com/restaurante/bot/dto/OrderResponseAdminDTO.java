package com.restaurante.bot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponseAdminDTO {

    private int mesa;
    private int statusMesa;
    private List<OrderDTO> orders;
    private List<OrderDTO> sentOrders;
    private Double totalGeneral;
    private Long transactionId;
}
