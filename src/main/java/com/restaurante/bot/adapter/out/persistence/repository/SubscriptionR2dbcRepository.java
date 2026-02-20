package com.restaurante.bot.adapter.out.persistence.repository;

import com.restaurante.bot.adapter.out.persistence.entity.SubscriptionEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface SubscriptionR2dbcRepository extends ReactiveCrudRepository<SubscriptionEntity, Long> {
    Flux<SubscriptionEntity> findByUserId(Long userId);
}
