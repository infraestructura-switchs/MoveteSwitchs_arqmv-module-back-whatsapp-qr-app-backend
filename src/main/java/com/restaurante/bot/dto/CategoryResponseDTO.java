package com.restaurante.bot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponseDTO {

    private Long categoryId;
    private String name;
    private Long parameterId;
    private String parameterName;
    private String status;
    private Long companyId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}