package com.restaurante.bot.adapter.out.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("product")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductEntity {

    @Id
    @Column("product_id")
    private Long productId;

    @Column("arq_product_id")
    private Integer arqProductId;

    @Column("name")
    private String name;

    @Column("price")
    private Double price;

    @Column("description")
    private String description;

    @Column("category_id")
    private Long categoryId;

    @Column("GRUPO_ID")
    private Long groupId;

    @Column("company_id")
    private Long companyId;

    @Column("status")
    private String status;

    @Column("img_product")
    private String imgProduct;

    @Column("COMMENTS")
    private String comments;

    @Column("INFORMATION")
    private String information;

    @Column("PREPARATION_TIME")
    private Integer preparationTime;

    @Column("soft_restaurant_id")
    private String softRestaurantId;
}
