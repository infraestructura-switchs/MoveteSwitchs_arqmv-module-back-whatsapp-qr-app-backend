package com.restaurante.bot.adapter.out.persistence.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import java.util.Date;

@Table("payment")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentEntity {
    @Id
    @Column("payment_id")
    private Long id;
    @Column("payment_method")
    private String namePayment;
    @Column("order_id")
    private Long orderId;
    @Column("amount")
    private Double amount;
    @Column("date")
    private Date date;
}
