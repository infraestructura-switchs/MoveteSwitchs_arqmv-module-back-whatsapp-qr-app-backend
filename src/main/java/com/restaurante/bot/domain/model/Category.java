package com.restaurante.bot.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Category {
    private Long categoryId;
    private String name;
    private Long externalId;
    private String status;
    private Long companyId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
