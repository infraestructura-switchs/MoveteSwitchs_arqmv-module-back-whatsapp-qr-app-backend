package com.restaurante.bot.adapter.out.persistence.repository;

import com.restaurante.bot.adapter.out.persistence.entity.RolEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface RolR2dbcRepository extends ReactiveCrudRepository<RolEntity, Long> {
}
