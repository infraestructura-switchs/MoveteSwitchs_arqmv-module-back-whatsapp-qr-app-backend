package com.restaurante.bot.adapter.out.persistence.repository;

import com.restaurante.bot.adapter.out.persistence.entity.CategoryMappingEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface CategoryMappingR2dbcRepository extends ReactiveCrudRepository<CategoryMappingEntity, Long> {
    Flux<CategoryMappingEntity> findByCompanyId(Long companyId);

    Flux<CategoryMappingEntity> findByCategoryId(Long categoryId);

    Flux<CategoryMappingEntity> findByGroupId(Long groupId);
}
