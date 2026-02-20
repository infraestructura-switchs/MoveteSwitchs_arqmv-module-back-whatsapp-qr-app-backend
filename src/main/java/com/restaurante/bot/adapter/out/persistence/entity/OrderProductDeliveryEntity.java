package com.restaurante.bot.adapter.out.persistence.entity;

import lombok.*;
import org.springframework.data.annotation.*;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("order_product_delivery")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderProductDeliveryEntity {
    @Id
    @Column("order_product_delivery_id")
    private Long orderProductId;
    @Column("order_transaction_delivery_id")
    private Long orderTransactionDeliveryId;
    @Column("product_id")
    private Long productId;
    @Column("name")
    private String name;
    @Column("quantity")
    private Long quantity;
    @Column("unit_price")
    private Double unitPrice;
    @Column("COMMENT_PRODUCT")
    private String commentProduct;
}
