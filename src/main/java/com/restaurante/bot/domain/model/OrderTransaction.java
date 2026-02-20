package com.restaurante.bot.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderTransaction {
    private Long orderTransactionId;
    private Long orderId;
    private Long transactionId;
    private Long companyId;
}
