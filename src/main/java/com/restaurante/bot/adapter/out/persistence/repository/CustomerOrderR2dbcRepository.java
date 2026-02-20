package com.restaurante.bot.adapter.out.persistence.repository;

import com.restaurante.bot.adapter.out.persistence.entity.CustomerOrderEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface CustomerOrderR2dbcRepository extends ReactiveCrudRepository<CustomerOrderEntity, Long> {
    Flux<CustomerOrderEntity> findByCustomerId(Long customerId);

    Flux<CustomerOrderEntity> findByCompanyId(Long companyId);
}
