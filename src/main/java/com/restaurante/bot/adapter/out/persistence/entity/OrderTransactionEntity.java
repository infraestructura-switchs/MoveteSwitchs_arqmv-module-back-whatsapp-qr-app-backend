package com.restaurante.bot.adapter.out.persistence.entity;

import lombok.*;
import org.springframework.data.annotation.*;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("order_transaction")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderTransactionEntity {
    @Id
    @Column("order_transaction_id")
    private Long orderTransactionId;
    @Column("order_id")
    private Long orderId;
    @Column("transaction_id")
    private Long transactionId;
    @Column("company_id")
    private Long companyId;
}
