package com.restaurante.bot.adapter.out.persistence.repository;

import com.restaurante.bot.adapter.out.persistence.entity.ParameterEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface ParameterR2dbcRepository extends ReactiveCrudRepository<ParameterEntity, Long> {
    Flux<ParameterEntity> findByCompanyId(Long companyId);

    Flux<ParameterEntity> findByCompanyIdAndStatus(Long companyId, String status);
}
