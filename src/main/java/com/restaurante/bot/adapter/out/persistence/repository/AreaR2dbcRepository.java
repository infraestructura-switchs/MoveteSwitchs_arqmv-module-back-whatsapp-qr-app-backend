package com.restaurante.bot.adapter.out.persistence.repository;

import com.restaurante.bot.adapter.out.persistence.entity.AreaEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface AreaR2dbcRepository extends ReactiveCrudRepository<AreaEntity, Long> {
}
