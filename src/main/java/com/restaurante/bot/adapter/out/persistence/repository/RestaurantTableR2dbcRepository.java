package com.restaurante.bot.adapter.out.persistence.repository;

import com.restaurante.bot.adapter.out.persistence.entity.RestaurantTableEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RestaurantTableR2dbcRepository extends ReactiveCrudRepository<RestaurantTableEntity, Integer> {
    Flux<RestaurantTableEntity> findByCompanyId(Long companyId);

    Mono<RestaurantTableEntity> findByTableNumberAndCompanyId(Long tableNumber, Long companyId);
}
