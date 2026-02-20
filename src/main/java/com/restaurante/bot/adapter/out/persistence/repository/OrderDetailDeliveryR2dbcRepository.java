package com.restaurante.bot.adapter.out.persistence.repository;

import com.restaurante.bot.adapter.out.persistence.entity.OrderDetailDeliveryEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface OrderDetailDeliveryR2dbcRepository extends ReactiveCrudRepository<OrderDetailDeliveryEntity, Long> {
    Flux<OrderDetailDeliveryEntity> findByCustomerId(Long customerId);
}
