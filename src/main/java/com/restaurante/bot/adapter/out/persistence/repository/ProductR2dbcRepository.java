package com.restaurante.bot.adapter.out.persistence.repository;

import com.restaurante.bot.adapter.out.persistence.entity.ProductEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductR2dbcRepository extends ReactiveCrudRepository<ProductEntity, Long> {

    Flux<ProductEntity> findByCompanyIdOrderByNameAsc(Long companyId);

    Mono<ProductEntity> findByArqProductId(Integer productId);

    Mono<ProductEntity> findByArqProductIdAndCompanyId(Integer productId, Long companyId);

    @Query("""
            SELECT p.* FROM product p
            WHERE p.company_id = :companyId
              AND (:name IS NULL OR LOWER(p.name) LIKE CONCAT('%', LOWER(:name), '%')
                   OR LOWER(p.description) LIKE CONCAT('%', LOWER(:name), '%'))
              AND (:categoryId IS NULL OR p.category_id = :categoryId)
              AND (p.status IS NULL OR UPPER(p.status) <> 'INACTIVO')
            ORDER BY
              CASE WHEN :name IS NULL THEN 3
                   WHEN LOWER(p.name) LIKE CONCAT(LOWER(:name), '%') THEN 0
                   WHEN LOWER(p.name) LIKE CONCAT('%', LOWER(:name), '%') THEN 1
                   ELSE 3 END,
              p.name ASC
            """)
    Flux<ProductEntity> search(Long companyId, String name, Long categoryId);

    @Query("""
            SELECT p.* FROM product p
            WHERE p.company_id = :companyId
              AND (:categoryId IS NULL OR p.category_id = :categoryId)
              AND (:name IS NULL OR LOWER(p.name) LIKE CONCAT('%', LOWER(:name), '%')
                   OR LOWER(p.description) LIKE CONCAT('%', LOWER(:name), '%'))
              AND (p.status IS NULL OR UPPER(p.status) <> 'INACTIVO')
            ORDER BY
              CASE WHEN :sort = 'ASC' THEN p.price END ASC,
              CASE WHEN :sort = 'DESC' THEN p.price END DESC,
              p.name ASC
            """)
    Flux<ProductEntity> findAllByCompanyAndCategoryAndNameOrderByPrice(Long companyId, Long categoryId, String name,
            String sort);
}
