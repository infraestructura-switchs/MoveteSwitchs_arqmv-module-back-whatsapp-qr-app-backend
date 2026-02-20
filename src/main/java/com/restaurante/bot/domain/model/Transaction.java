package com.restaurante.bot.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Transaction {
    private Long transactionId;
    private Integer tableId;
    private Integer paymentId;
    private Long ratingId;
    private Double transactionTotal;
    private Long status;
    private Long companyId;
}
