package com.restaurante.bot.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "order_transaction")
public class OrderTransaction {

    @Id
    @SequenceGenerator(name = "ORDER-TRANSACTION-SEQ", sequenceName = "ORDER_TRANSACTION_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ORDER-TRANSACTION-SEQ")
    @Column(name = "order_transaction_id")
    private Long orderTransactionId;

    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "transaction_id")
    private Long transactionId;

    @Column(name = "company_id")
    private Long companyId;

}
