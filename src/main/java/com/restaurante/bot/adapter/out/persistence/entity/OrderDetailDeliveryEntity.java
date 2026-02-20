package com.restaurante.bot.adapter.out.persistence.entity;

import lombok.*;
import org.springframework.data.annotation.*;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import java.time.LocalDateTime;

@Table("order_transaction_delivery")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDetailDeliveryEntity {
    @Id
    @Column("order_transaction_delivery_id")
    private Long orderTransactionDeliveryId;
    @Column("payment_id")
    private Long paymentId;
    @Column("customer_id")
    private Long customerId;
    @Column("method")
    private String method;
    @Column("total")
    private Double total;
    @Column("status")
    private String status;
    @Column("status_order")
    private String statusOrder;
    @CreatedDate
    @Column("created_at")
    private LocalDateTime createdAt;
    @LastModifiedDate
    @Column("updated_at")
    private LocalDateTime updatedAt;
}
