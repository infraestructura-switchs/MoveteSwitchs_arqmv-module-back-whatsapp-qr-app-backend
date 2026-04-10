package com.restaurante.bot.dto;

import com.restaurante.bot.util.SortConstants;
import com.restaurante.bot.util.StatusConstants;

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
    private String orders = SortConstants.ASC;
    private String sortBy = "id";
    private String status = StatusConstants.ACTIVE;

    /**
     * Validates pagination parameters
     */
    public void validate() {
        if (this.page < 0) this.page = 0;
        if (this.size <= 0) this.size = 5;
        if (this.size > 100) this.size = 100;
        
        if (this.orders == null || this.orders.isEmpty()) {
            this.orders = SortConstants.ASC;
        } else if (!this.orders.equalsIgnoreCase(SortConstants.ASC) && !this.orders.equalsIgnoreCase(SortConstants.DESC)) {
            this.orders = SortConstants.ASC;
        }
        
        if (this.sortBy == null || this.sortBy.isEmpty()) {
            this.sortBy = "id";
        }
    }
}
