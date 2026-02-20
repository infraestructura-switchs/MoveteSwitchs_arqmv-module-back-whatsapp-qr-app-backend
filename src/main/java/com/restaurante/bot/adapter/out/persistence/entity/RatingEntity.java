package com.restaurante.bot.adapter.out.persistence.entity;

import lombok.*;
import org.springframework.data.annotation.*;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import java.time.LocalDateTime;

@Table("rating")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RatingEntity {
    @Id
    @Column("rating_id")
    private Integer ratingId;
    @Column("table_id")
    private Integer tableId;
    @Column("qualification")
    private String qualification;
}
