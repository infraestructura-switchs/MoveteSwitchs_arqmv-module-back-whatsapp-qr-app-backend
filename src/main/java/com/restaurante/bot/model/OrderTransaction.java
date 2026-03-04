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
    @Column(name = "order_transaction_id")
    private Long orderTransactionId;

    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "transaction_id")
    private Long transactionId;

    @Column(name = "company_id")
    private Long companyId;

}
