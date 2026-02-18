package com.restaurante.bot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponseDTO {

    private int mesa;
    private int statusMesa;
    private List<OrderDTO> orders;
    private Double totalGeneral;
    private Long transactionId;
}
