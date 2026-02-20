package com.restaurante.bot.adapter.out.persistence.repository;

import com.restaurante.bot.adapter.out.persistence.entity.TransactionClientEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface TransactionClientR2dbcRepository extends ReactiveCrudRepository<TransactionClientEntity, Long> {
    Flux<TransactionClientEntity> findByTransactionId(Long transactionId);

    Flux<TransactionClientEntity> findByCustomerId(Long customerId);
}
