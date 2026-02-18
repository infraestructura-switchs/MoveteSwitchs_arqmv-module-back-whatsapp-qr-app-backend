package com.restaurante.bot.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "order_product_delivery")
public class OrderProductDelivery {

    @Id
    @SequenceGenerator(name = "ORDER-PRODUCT-DELIVERY-SEQ", sequenceName = "ORDER_PRODUCT_DELIVERY_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ORDER-PRODUCT-DELIVERY-SEQ")
    @Column(name = "order_product_delivery_id")
    private Long orderProductId;

    @Column(name = "order_transaction_delivery_id")
    private Long orderTransactionDeliveryId;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "name")
    private String name;

    @Column(name = "quantity")
    private Long quantity;

    @Column(name = "unit_price")
    private Double unitPrice;

    @Column(name = "COMMENT_PRODUCT")
    private String commentProduct;



}
