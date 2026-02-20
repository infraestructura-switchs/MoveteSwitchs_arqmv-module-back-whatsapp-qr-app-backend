package com.restaurante.bot.adapter.out.persistence.repository;

import com.restaurante.bot.adapter.out.persistence.entity.RatingEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface RatingR2dbcRepository extends ReactiveCrudRepository<RatingEntity, Integer> {
    Flux<RatingEntity> findByTableId(Integer tableId);
}
