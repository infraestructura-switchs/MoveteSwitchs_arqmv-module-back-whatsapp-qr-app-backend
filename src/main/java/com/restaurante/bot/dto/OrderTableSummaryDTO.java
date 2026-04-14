package com.restaurante.bot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * DTO for transaction table summary
 * Groups orders by table with status and total information
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderTableSummaryDTO {
    private Long tableNumber;
    private String tableStatus;
    private Double totalAmount;
    private Long transactionId;
    private List<OrderLineItemDTO> lineItems;
    private Integer orderStatus;
}
