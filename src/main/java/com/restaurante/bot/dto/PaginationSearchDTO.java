package com.restaurante.bot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Base DTO for pagination and sorting parameters used in searches
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaginationSearchDTO {
    private int page = 0;
    private int size = 5;
    private String orders = "ASC";
    private String sortBy = "id";
    private String status = "ACTIVE";

    /**
     * Validates pagination parameters
     */
    public void validate() {
        if (this.page < 0) this.page = 0;
        if (this.size <= 0) this.size = 5;
        if (this.size > 100) this.size = 100;
        
        if (this.orders == null || this.orders.isEmpty()) {
            this.orders = "ASC";
        } else if (!this.orders.equalsIgnoreCase("ASC") && !this.orders.equalsIgnoreCase("DESC")) {
            this.orders = "ASC";
        }
        
        if (this.sortBy == null || this.sortBy.isEmpty()) {
            this.sortBy = "id";
        }
    }
}
