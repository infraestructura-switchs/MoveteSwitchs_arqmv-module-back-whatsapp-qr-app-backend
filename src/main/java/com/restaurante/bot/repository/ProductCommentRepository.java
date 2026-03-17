package com.restaurante.bot.repository;

import com.restaurante.bot.model.ProductComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;

public interface ProductCommentRepository extends JpaRepository<ProductComment, Long> {
    List<ProductComment> findByProductId(BigDecimal productId);
}
