package com.restaurante.bot.adapter.out.persistence.repository;

import com.restaurante.bot.adapter.out.persistence.entity.HistoryEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface HistoryR2dbcRepository extends ReactiveCrudRepository<HistoryEntity, Integer> {
    Flux<HistoryEntity> findByTransactionId(Long transactionId);
}
