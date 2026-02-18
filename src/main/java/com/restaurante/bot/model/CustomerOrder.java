package com.restaurante.bot.model;

import jakarta.persistence.*;
import jdk.jfr.Unsigned;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "customer_order")
public class CustomerOrder {

    @Id
    @SequenceGenerator(name = "CUSTOMER-ORDER-SEQ", sequenceName = "CUSTOMER_ORDER_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CUSTOMER-ORDER-SEQ")
    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "status")
    private Integer status;

    @Column(name = "customer_order_date")
    private LocalDateTime date;

    @Column(name = "company_id")
    private Long companyId;

    @Column(name = "total")
    private Double total;

   @Column(name = "customer_id")
    private Long customerId;


}