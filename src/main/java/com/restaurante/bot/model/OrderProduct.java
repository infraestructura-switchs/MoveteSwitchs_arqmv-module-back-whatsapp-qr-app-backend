package com.restaurante.bot.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "order_product")
public class OrderProduct {


    @Id
    @SequenceGenerator(name = "ORDER-PRODUCT-SEQ", sequenceName = "ORDER_PRODUCT_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ORDER-PRODUCT-SEQ")
    @Column(name = "order_product_id")
    private Integer orderProductId;

    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "product_id")
    private String productId;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "COMMENT_PRODUCT")
    private String commentProduct;

    @Column(name = "company_id")
    private Long companyId;

}
