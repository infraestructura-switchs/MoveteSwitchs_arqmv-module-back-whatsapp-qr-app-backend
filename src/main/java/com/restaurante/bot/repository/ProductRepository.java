package com.restaurante.bot.repository;

import com.restaurante.bot.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
  List<Product> findByCompanyIdOrderByNameAsc(Long companyId);

  Optional<Product> findByProductIntegrationId(Long productIntegrationId);

  @Query("SELECT p FROM Product p WHERE p.productIntegration.arqProductId = :productId")
  Optional<Product> findByArqProductId(@Param("productId") Integer productId);

  @Query(value = """
      SELECT p FROM Product p
      WHERE
        p.companyId = :companyId
        AND (
          :name IS NULL
          OR (LOWER(p.name) LIKE LOWER(CONCAT(:name, '%'))
            OR LOWER(p.name) LIKE LOWER(CONCAT('%', CONCAT(:name, '%')))
            OR LOWER(p.description) LIKE LOWER(CONCAT('%', CONCAT(:name, '%')))
          )
        )
        AND (:categoryId IS NULL OR p.categoryId = :categoryId)
        AND (p.status IS NULL OR UPPER(p.status) <> 'INACTIVE')
      ORDER BY
        CASE
          WHEN :name IS NULL THEN 3
          WHEN LOWER(p.name) LIKE LOWER(CONCAT(:name, '%')) THEN 0
          WHEN LOWER(p.name) LIKE LOWER(CONCAT('%', CONCAT(:name, '%'))) THEN 1
          WHEN LOWER(p.description) LIKE LOWER(CONCAT('%', CONCAT(:name, '%'))) THEN 2
          ELSE 3
        END,
        p.name ASC
      """,
      countQuery = "SELECT COUNT(p) FROM Product p WHERE p.companyId = :companyId AND (:name IS NULL OR (LOWER(p.name) LIKE LOWER(CONCAT(:name, '%')) OR LOWER(p.name) LIKE LOWER(CONCAT('%', CONCAT(:name, '%'))) OR LOWER(p.description) LIKE LOWER(CONCAT('%', CONCAT(:name, '%'))))) AND (:categoryId IS NULL OR p.categoryId = :categoryId) AND (p.status IS NULL OR UPPER(p.status) <> 'INACTIVE')"
  )
  org.springframework.data.domain.Page<Product> search(
      @Param("companyId") Long companyId,
      @Param("name") String name,
      @Param("categoryId") Long categoryId,
      org.springframework.data.domain.Pageable pageable);

  @Query(value = """
        SELECT p
        FROM Product p
        WHERE p.companyId = :companyId
          AND (:categoryId IS NULL OR p.categoryId = :categoryId)
          AND (
            :name IS NULL
            OR (
              LOWER(p.name) LIKE LOWER(CONCAT(:name, '%'))
              OR LOWER(p.name) LIKE LOWER(CONCAT('%', CONCAT(:name, '%')))
              OR LOWER(p.description) LIKE LOWER(CONCAT('%', CONCAT(:name, '%')))
            )
          )
          AND (p.status IS NULL OR UPPER(p.status) <> 'INACTIVE')
      """,
      countQuery = "SELECT COUNT(p) FROM Product p WHERE p.companyId = :companyId AND (:categoryId IS NULL OR p.categoryId = :categoryId) AND (:name IS NULL OR (LOWER(p.name) LIKE LOWER(CONCAT(:name, '%')) OR LOWER(p.name) LIKE LOWER(CONCAT('%', CONCAT(:name, '%'))) OR LOWER(p.description) LIKE LOWER(CONCAT('%', CONCAT(:name, '%'))))) AND (p.status IS NULL OR UPPER(p.status) <> 'INACTIVE')"
  )
    org.springframework.data.domain.Page<Product> findAllByCompanyAndCategoryAndNameOrderByPrice(
      @Param("companyId") Long companyId,
      @Param("categoryId") Long categoryId,
      @Param("name") String name,
      org.springframework.data.domain.Pageable pageable);

  @Query("SELECT p FROM Product p WHERE p.companyId = :companyId AND p.productIntegration.arqProductId = :productId")
  Optional<Product> findByArqProductIdAndCompanyId(@Param("productId") Integer productId, @Param("companyId") Long companyId);

  // Backwards-compatible overloads that return full lists (use with care)
  default List<Product> search(Long companyId, String name, Long categoryId) {
    Pageable pageable = PageRequest.of(0, 1000);
    return search(companyId, name, categoryId, pageable).getContent();
  }

  default List<Product> findAllByCompanyAndCategoryAndNameOrderByPrice(Long companyId, Long categoryId, String name) {
    Pageable pageable = PageRequest.of(0, 1000);
    return findAllByCompanyAndCategoryAndNameOrderByPrice(companyId, categoryId, name, pageable).getContent();
  }

}
