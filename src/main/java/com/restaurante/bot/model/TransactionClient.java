package com.restaurante.bot.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "TRANSACTION_CLIENT")
public class TransactionClient {

    @Id
    @SequenceGenerator(name = "transaction-customer-seq", sequenceName = "transaction_customer_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transaction-customer-seq")
    @Column(name = "TRANSACTION_CLIENT_ID")
    private Long transactionClientId;

    @Column(name = "TRANSACTION_ID")
    private Long transactionId;


    @Column(name = "CUSTOMER_ID")
    private Long customerId;



}
