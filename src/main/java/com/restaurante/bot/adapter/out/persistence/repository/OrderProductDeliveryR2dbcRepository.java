package com.restaurante.bot.adapter.out.persistence.repository;

import com.restaurante.bot.adapter.out.persistence.entity.OrderProductDeliveryEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface OrderProductDeliveryR2dbcRepository extends ReactiveCrudRepository<OrderProductDeliveryEntity, Long> {
    Flux<OrderProductDeliveryEntity> findByOrderTransactionDeliveryId(Long orderTransactionDeliveryId);
}
