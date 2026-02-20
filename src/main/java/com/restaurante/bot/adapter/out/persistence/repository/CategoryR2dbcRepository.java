package com.restaurante.bot.adapter.out.persistence.repository;

import com.restaurante.bot.adapter.out.persistence.entity.CategoryEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CategoryR2dbcRepository extends ReactiveCrudRepository<CategoryEntity, Long> {

    Mono<CategoryEntity> findByName(String name);

    Mono<CategoryEntity> findByNameAndCompanyId(String name, Long companyId);

    Flux<CategoryEntity> findByStatus(String status);

    Mono<CategoryEntity> findByExternalId(Long externalId);

    @Query("SELECT * FROM category WHERE LOWER(name) LIKE CONCAT('%', LOWER(:name), '%')")
    Flux<CategoryEntity> findByNameContainingIgnoreCase(String name);

    Flux<CategoryEntity> findByCompanyId(Long companyId);

    Flux<CategoryEntity> findByCompanyIdAndStatus(Long companyId, String status);

    Flux<CategoryEntity> findByCompanyIdAndExternalId(Long companyId, Long externalId);

    @Query("SELECT COUNT(*) > 0 FROM category WHERE name = :name")
    Mono<Boolean> existsByName(String name);

    @Query("SELECT COUNT(*) > 0 FROM category WHERE name = :name AND company_id = :companyId")
    Mono<Boolean> existsByNameAndCompanyId(String name, Long companyId);

    @Query("SELECT * FROM category WHERE company_id = :companyId AND LOWER(name) = LOWER(:name) LIMIT 1")
    Mono<CategoryEntity> findByCompanyIdAndNameIgnoreCase(Long companyId, String name);
}
