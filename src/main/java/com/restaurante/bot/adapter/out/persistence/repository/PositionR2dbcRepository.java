package com.restaurante.bot.adapter.out.persistence.repository;

import com.restaurante.bot.adapter.out.persistence.entity.PositionEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface PositionR2dbcRepository extends ReactiveCrudRepository<PositionEntity, Long> {
}
