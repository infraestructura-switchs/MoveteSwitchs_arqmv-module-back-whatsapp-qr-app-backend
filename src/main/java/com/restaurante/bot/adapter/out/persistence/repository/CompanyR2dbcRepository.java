package com.restaurante.bot.adapter.out.persistence.repository;

import com.restaurante.bot.adapter.out.persistence.entity.CompanyEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CompanyR2dbcRepository extends ReactiveCrudRepository<CompanyEntity, Long> {

    @Query("SELECT * FROM company WHERE status = 'ACTIVE'")
    Flux<CompanyEntity> findAllActive();

    Mono<Boolean> existsByExternalCompanyId(Long externalCompanyId);

    Mono<CompanyEntity> findByExternalCompanyId(Long externalCompanyId);

    @Query("SELECT external_company_id FROM company WHERE status = 'ACTIVE'")
    Flux<Long> findCompanyIds();

    @Query("SELECT * FROM company WHERE status = 'ACTIVE' LIMIT :limit OFFSET :offset")
    Flux<CompanyEntity> findAllActivePaged(int limit, long offset);

    @Query("SELECT COUNT(*) FROM company WHERE status = 'ACTIVE'")
    Mono<Long> countAllActive();
}
