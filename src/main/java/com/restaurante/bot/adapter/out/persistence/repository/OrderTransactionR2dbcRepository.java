package com.restaurante.bot.adapter.out.persistence.repository;

import com.restaurante.bot.adapter.out.persistence.entity.OrderTransactionEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface OrderTransactionR2dbcRepository extends ReactiveCrudRepository<OrderTransactionEntity, Long> {
    Flux<OrderTransactionEntity> findByTransactionId(Long transactionId);

    Flux<OrderTransactionEntity> findByOrderId(Long orderId);

    Flux<OrderTransactionEntity> findByCompanyId(Long companyId);
}
