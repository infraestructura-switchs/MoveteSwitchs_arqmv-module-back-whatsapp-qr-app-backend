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
@Table(name = "product")
public class Product {

    @Id
    @SequenceGenerator(name = "PRODUCT-SEQ", sequenceName = "PRODUCT_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PRODUCT-SEQ")
    @Column(name = "product_id")
    private Long productId;

    @Column(name = "arq_product_id")
    private Integer arqProductId;

    @Column(name = "name")
    private String name;

    @Column(name = "price")
    private Double price;

    @Column(name = "description")
    private String description;

    @Column(name = "category_id")
    private Long categoryId;

    @Column(name = "GRUPO_ID")
    private Long groupId;

    @Column(name = "company_id")
    private Long companyId;

    @Column(name = "status")
    private String status;

    @Column(name = "img_product")
    private String imgProduct;

    @Column(name = "COMMENTS")
    private String comments;

    @Column(name = "INFORMATION")
    private String information;

    @Column(name = "PREPARATION_TIME")
    private Integer preparationTime;

    @Column(name = "soft_restaurant_id")
    private String softRestaurantId;

}
