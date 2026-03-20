package com.restaurante.bot.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "product_integration")
public class ProductIntegration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_integration_id")
    private Long productIntegrationId;

    @Column(name = "arq_product_id")
    private Integer arqProductId;

    @Column(name = "name")
    private String name;

    @Column(name = "price")
    private Double price;

    @Column(name = "original_price")
    private Double originalPrice;

    @Column(name = "description")
    private String description;

    @Column(name = "grupo_id")
    private Long groupId;

    @Column(name = "company_id")
    private Long companyId;

    @Column(name = "status")
    private String status;

    @Column(name = "img_product")
    private String imgProduct;

    @Column(name = "category_id")
    private Long categoryId;

    @Column(name = "comments")
    private String comments;

    @Column(name = "information")
    private String information;

    @Column(name = "preparation_time")
    private Integer preparationTime;

    @Column(name = "soft_restaurant_id")
    private String softRestaurantId;
}
