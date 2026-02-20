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
public class CategoryMapping {
    private Long mappingId;
    private Long groupId;
    private Long categoryId;
    private Long companyId;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
