package com.restaurante.bot.adapter.out.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("order_product")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderProductEntity {

    @Id
    @Column("order_product_id")
    private Integer orderProductId;

    @Column("order_id")
    private Long orderId;

    @Column("product_id")
    private String productId;

    @Column("quantity")
    private Integer quantity;

    @Column("COMMENT_PRODUCT")
    private String commentProduct;

    @Column("company_id")
    private Long companyId;
}
