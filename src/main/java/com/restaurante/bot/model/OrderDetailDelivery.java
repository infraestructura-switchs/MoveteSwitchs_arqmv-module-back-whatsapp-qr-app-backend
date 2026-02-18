package com.restaurante.bot.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "order_transaction_delivery")
public class OrderDetailDelivery {
    @Id
    @SequenceGenerator(name = "ORDER-TRANSACTION-DELIVERY-SEQ", sequenceName = "ORDER_TRANSACTION_DELIVERY_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ORDER-TRANSACTION-DELIVERY-SEQ")
    @Column(name = "order_transaction_delivery_id")
    private Long orderTransactionDeliveryId;

    @Column(name = "payment_id")
    private Long paymentId;

    @Column(name = "customer_id")
    private Long customerId;

    @Column(name = "method")
    private String method;

    @Column(name = "total")
    private Double total;

    @Column(name = "status")
    private String status;

    @Column(name = "status_order")
    private String statusOrder;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}
