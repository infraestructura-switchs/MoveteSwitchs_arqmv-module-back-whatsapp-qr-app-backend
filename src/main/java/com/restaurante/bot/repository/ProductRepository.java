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

    @Query(
            value = """
    SELECT p.* FROM product p
    WHERE
      p.company_id = :companyId
      AND (
        :name IS NULL
        OR (LOWER(p.name) LIKE LOWER(:name || '%')
          OR INSTR(LOWER(p.name), LOWER(:name)) > 0
          OR INSTR(LOWER(DBMS_LOB.SUBSTR(p.description, 4000, 1)),
               LOWER(:name)
             ) > 0
        )
      )
      AND (:categoryId IS NULL OR p.category_id = :categoryId)
      AND (p.status IS NULL OR UPPER(p.status) <> 'INACTIVO')
    ORDER BY
      CASE
        WHEN :name IS NULL THEN 3
        WHEN LOWER(p.name) LIKE LOWER(:name || '%') THEN 0  
        WHEN INSTR(LOWER(p.name), LOWER(:name)) > 0 THEN 1  
        WHEN INSTR(
               LOWER(DBMS_LOB.SUBSTR(p.description, 4000, 1)),
               LOWER(:name)
             ) > 0 THEN 2                                   
        ELSE 3
      END,
      CASE
        WHEN :name IS NULL THEN NULL
        ELSE NULLIF(INSTR(LOWER(p.name), LOWER(:name)), 0)
      END,
      p.name ASC
    """,
            nativeQuery = true
    )
    List<Product> search(
            @Param("companyId") Long companyId,
            @Param("name") String name,
            @Param("categoryId") Long categoryId
    );

    @Query(value = """
  SELECT p.* 
  FROM product p
  WHERE p.company_id = :companyId
    AND (:categoryId IS NULL OR p.category_id = :categoryId) 
    AND (
      :name IS NULL 
      OR (
        LOWER(p.name) LIKE LOWER(:name || '%') 
        OR INSTR(LOWER(p.name), LOWER(:name)) > 0 
        OR INSTR(LOWER(DBMS_LOB.SUBSTR(p.description, 4000, 1)), LOWER(:name)) > 0 
      )
    )
    AND (p.status IS NULL OR UPPER(p.status) <> 'INACTIVO') 
  ORDER BY
    CASE
      WHEN :sort = 'ASC' THEN p.price END ASC,  
    CASE
      WHEN :sort = 'DESC' THEN p.price END DESC,  
    p.name ASC 
""", nativeQuery = true)
    List<Product> findAllByCompanyAndCategoryAndNameOrderByPrice(
            @Param("companyId") Long companyId,
            @Param("categoryId") Long categoryId,
            @Param("name") String name,
            @Param("sort") String sort
    );


    Optional<Product>findByArqProductIdAndCompanyId(Integer productId, Long companyId);


}
