package com.restaurante.bot.adapter.out.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("transaction")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionEntity {

    @Id
    @Column("transaction_id")
    private Long transactionId;

    @Column("table_id")
    private Integer tableId;

    @Column("payment_id")
    private Integer paymentId;

    @Column("rating_id")
    private Long ratingId;

    @Column("transaction_total")
    private Double transactionTotal;

    @Column("status")
    private Long status;

    @Column("company_id")
    private Long companyId;
}
