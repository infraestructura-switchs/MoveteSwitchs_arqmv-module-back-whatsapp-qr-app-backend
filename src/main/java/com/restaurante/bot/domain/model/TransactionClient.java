package com.restaurante.bot.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionClient {
    private Long transactionClientId;
    private Long transactionId;
    private Long customerId;
}
