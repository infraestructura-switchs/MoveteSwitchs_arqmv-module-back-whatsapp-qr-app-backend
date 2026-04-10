package com.restaurante.bot.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO for Product search operations
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductSearchDTO extends PaginationSearchDTO {
    private Long companyId;
    private Long externalCompanyId;
    private String name;
    private Long categoryId;

    /**
     * Sets default sort field for Product
     */
    @Override
    public void validate() {
        if (this.getPage() < 0) this.setPage(0);
        if (this.getSize() <= 0) this.setSize(5);
        if (this.getSize() > 100) this.setSize(100);
        
        if (this.getOrders() == null || this.getOrders().isEmpty()) {
            this.setOrders("ASC");
        }
        
        if (this.getSortBy() == null || this.getSortBy().isEmpty()) {
            this.setSortBy("productId");
        }
    }
}
