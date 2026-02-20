package com.restaurante.bot.adapter.out.persistence.entity;

import lombok.*;
import org.springframework.data.annotation.*;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("TRANSACTION_CLIENT")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionClientEntity {
    @Id
    @Column("TRANSACTION_CLIENT_ID")
    private Long transactionClientId;
    @Column("TRANSACTION_ID")
    private Long transactionId;
    @Column("CUSTOMER_ID")
    private Long customerId;
}
