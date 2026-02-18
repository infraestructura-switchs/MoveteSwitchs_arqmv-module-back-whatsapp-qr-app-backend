package com.restaurante.bot.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "payment")
public class Payment {

    @Id
    @SequenceGenerator(name = "PAYMENT-SEQ", sequenceName = "PAYMENT_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PAYMENT-SEQ")
    @Column(name = "payment_id")
    private Long id;

    @Column(name = "payment_method")
    private String namePayment;

    @Column(name = "order_id")
    private Long orderid;

    @Column(name = "amount")
    private Double amount;

    @Column(name = "date")
    private Date date;



}
