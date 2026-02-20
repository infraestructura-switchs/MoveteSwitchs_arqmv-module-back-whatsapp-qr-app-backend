package com.restaurante.bot.adapter.out.persistence.repository;

import com.restaurante.bot.adapter.out.persistence.entity.WaiterCallEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface WaiterCallR2dbcRepository extends ReactiveCrudRepository<WaiterCallEntity, Integer> {
    Flux<WaiterCallEntity> findByTableId(Integer tableId);
}
