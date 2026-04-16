package com.restaurante.bot.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "TRANSACTION_CLIENT")
public class TransactionClient {

    @Id
    @Column(name = "TRANSACTION_CLIENT_ID")
    private Long transactionClientId;

    @Column(name = "TRANSACTION_ID")
    private Long transactionId;

    @Column(name = "CUSTOMER_ID")
    private Long customerId;

}
