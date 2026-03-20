package com.restaurante.bot.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "product")
public class Product {

    @Id
    @Column(name = "product_id")
    private Long productId;

    @Column(name = "name")
    private String name;

    @Column(name = "price")
    private Double price;

    @Column(name = "original_price")
    private Double originalPrice;

    @Column(name = "description")
    private String description;

    @Column(name = "category_id")
    private Long categoryId;

    @Column(name = "company_id")
    private Long companyId;

    @Column(name = "status")
    private String status;

    @Column(name = "img_product")
    private String imgProduct;

    @Column(name = "COMMENTS")
    private String comments;

    @OneToMany(cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true, fetch = jakarta.persistence.FetchType.LAZY)
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    private java.util.List<ProductComment> productComments = new java.util.ArrayList<>();

    @Column(name = "INFORMATION")
    private String information;

    @Column(name = "PREPARATION_TIME")
    private Integer preparationTime;

    @Column(name = "product_integration_id", insertable = false, updatable = false)
    private Long productIntegrationId;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "product_integration_id", referencedColumnName = "product_integration_id")
    private ProductIntegration productIntegration;

    public Integer getArqProductId() {
        return productIntegration != null ? productIntegration.getArqProductId() : null;
    }

    public void setArqProductId(Integer arqProductId) {
        ensureIntegration().setArqProductId(arqProductId);
    }

    public Long getGroupId() {
        return productIntegration != null ? productIntegration.getGroupId() : null;
    }

    public void setGroupId(Long groupId) {
        ensureIntegration().setGroupId(groupId);
    }

    public String getSoftRestaurantId() {
        return productIntegration != null ? productIntegration.getSoftRestaurantId() : null;
    }

    public void setSoftRestaurantId(String softRestaurantId) {
        ensureIntegration().setSoftRestaurantId(softRestaurantId);
    }

    private ProductIntegration ensureIntegration() {
        if (productIntegration == null) {
            productIntegration = new ProductIntegration();
        }
        return productIntegration;
    }

}
