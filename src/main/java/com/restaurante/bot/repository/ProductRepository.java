package com.restaurante.bot.repository;

import com.restaurante.bot.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

  List<Product> findByCompanyIdOrderByNameAsc(Long companyId);

  Optional<Product> findByArqProductId(Integer productId);

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
        AND (p.status IS NULL OR UPPER(p.status) <> 'INACTIVO')
      ORDER BY
        CASE
          WHEN :name IS NULL THEN 3
          WHEN LOWER(p.name) LIKE LOWER(CONCAT(:name, '%')) THEN 0
          WHEN LOWER(p.name) LIKE LOWER(CONCAT('%', CONCAT(:name, '%'))) THEN 1
          WHEN LOWER(p.description) LIKE LOWER(CONCAT('%', CONCAT(:name, '%'))) THEN 2
          ELSE 3
        END,
        p.name ASC
      """)
  List<Product> search(
      @Param("companyId") Long companyId,
      @Param("name") String name,
      @Param("categoryId") Long categoryId);

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
          AND (p.status IS NULL OR UPPER(p.status) <> 'INACTIVO')
        ORDER BY
          CASE
            WHEN :sort = 'ASC' THEN p.price END ASC,
          CASE
            WHEN :sort = 'DESC' THEN p.price END DESC,
          p.name ASC
      """)
  List<Product> findAllByCompanyAndCategoryAndNameOrderByPrice(
      @Param("companyId") Long companyId,
      @Param("categoryId") Long categoryId,
      @Param("name") String name,
      @Param("sort") String sort);

  Optional<Product> findByArqProductIdAndCompanyId(Integer productId, Long companyId);

}
