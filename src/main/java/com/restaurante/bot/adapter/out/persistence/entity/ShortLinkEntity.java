package com.restaurante.bot.adapter.out.persistence.entity;

import lombok.*;
import org.springframework.data.annotation.*;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import java.time.LocalDateTime;

@Table("short_links")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShortLinkEntity {
    @Id
    @Column("short_links_id")
    private Long shortLinksId;
    @Column("original_url")
    private String originalUrl;
    @Column("short_code")
    private String shortCode;
    @CreatedDate
    @Column("created_at")
    private LocalDateTime createdAt;
    @LastModifiedDate
    @Column("updated_at")
    private LocalDateTime updatedAt;
}
