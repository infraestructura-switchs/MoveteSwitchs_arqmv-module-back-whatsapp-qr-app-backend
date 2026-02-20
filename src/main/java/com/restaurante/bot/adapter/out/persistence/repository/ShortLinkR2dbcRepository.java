package com.restaurante.bot.adapter.out.persistence.repository;

import com.restaurante.bot.adapter.out.persistence.entity.ShortLinkEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface ShortLinkR2dbcRepository extends ReactiveCrudRepository<ShortLinkEntity, Long> {
    Mono<ShortLinkEntity> findByShortCode(String shortCode);

    Mono<ShortLinkEntity> findByOriginalUrl(String originalUrl);
}
