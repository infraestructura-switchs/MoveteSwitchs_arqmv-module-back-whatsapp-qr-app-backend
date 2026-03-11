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
    @Column(name = "payment_id")
    private Long paymentId;

    @Column(name = "payment_method")
    private String namePayment;

    @Column(name = "order_id")
    private Long orderid;

    @Column(name = "amount")
    private Double amount;

    @Column(name = "date")
    private Date date;

}
