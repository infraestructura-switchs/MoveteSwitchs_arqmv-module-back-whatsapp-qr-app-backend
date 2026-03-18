package com.restaurante.bot.repository;

import com.restaurante.bot.model.ProductDiscount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ProductDiscountRepository extends JpaRepository<ProductDiscount, Long> {

    @Query("""
        SELECT d
        FROM ProductDiscount d
        WHERE d.companyId = :companyId
          AND (:productId IS NULL OR d.productId = :productId)
          AND (:status IS NULL OR UPPER(d.status) = UPPER(:status))
        ORDER BY d.productId ASC, d.startAt DESC, d.productDiscountId DESC
        """)
    Page<ProductDiscount> findByFilters(
            @Param("companyId") Long companyId,
            @Param("productId") Long productId,
            @Param("status") String status,
            Pageable pageable);

    @Query("""
        SELECT d
        FROM ProductDiscount d
        WHERE d.companyId = :companyId
          AND d.productId IN :productIds
          AND UPPER(d.status) = 'ACTIVE'
          AND d.startAt <= :currentTime
          AND d.endAt >= :currentTime
        ORDER BY d.productId ASC, d.startAt DESC, d.productDiscountId DESC
        """)
    List<ProductDiscount> findActiveDiscounts(
            @Param("companyId") Long companyId,
            @Param("productIds") Collection<Long> productIds,
            @Param("currentTime") LocalDateTime currentTime);

    @Query("""
        SELECT d
        FROM ProductDiscount d
        WHERE d.companyId = :companyId
          AND d.productId = :productId
          AND UPPER(d.status) = 'ACTIVE'
          AND d.startAt <= :currentTime
          AND d.endAt >= :currentTime
        ORDER BY d.startAt DESC, d.productDiscountId DESC
        """)
    List<ProductDiscount> findActiveDiscountsForProduct(
            @Param("companyId") Long companyId,
            @Param("productId") Long productId,
            @Param("currentTime") LocalDateTime currentTime);

    Optional<ProductDiscount> findByProductDiscountIdAndCompanyId(Long productDiscountId, Long companyId);
}