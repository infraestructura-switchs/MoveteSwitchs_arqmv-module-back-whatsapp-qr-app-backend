package com.restaurante.bot.adapter.out.persistence.repository;

import com.restaurante.bot.adapter.out.persistence.entity.CustomerEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface CustomerR2dbcRepository extends ReactiveCrudRepository<CustomerEntity, Long> {
    Mono<CustomerEntity> findByPhone(String phone);
}
