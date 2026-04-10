package com.restaurante.bot.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO for User search operations
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserSearchDTO extends PaginationSearchDTO {
    private Long userId;
    private String name;
    private String email;
    private String login;
    private String companyName;
    private String positionDescription;
    private String areaDescription;

    /**
     * Sets default sort field for User
     */
    @Override
    public void validate() {
        if (this.getPage() < 0) this.setPage(0);
        if (this.getSize() <= 0) this.setSize(5);
        if (this.getSize() > 100) this.setSize(100);
        
        if (this.getOrders() == null || this.getOrders().isEmpty()) {
            this.setOrders(com.restaurante.bot.util.SortConstants.ASC);
        }
        
        if (this.getSortBy() == null || this.getSortBy().isEmpty()) {
            this.setSortBy("userId");
        }
        
        if (this.getStatus() == null || this.getStatus().isEmpty()) {
            this.setStatus(com.restaurante.bot.util.Constants.ACTIVE_STATUS);
        }
    }
}
