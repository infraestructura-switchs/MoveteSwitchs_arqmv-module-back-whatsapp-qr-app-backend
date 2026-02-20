package com.restaurante.bot.adapter.out.persistence.repository;

import com.restaurante.bot.adapter.out.persistence.entity.TypeIdentificationEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface TypeIdentificationR2dbcRepository extends ReactiveCrudRepository<TypeIdentificationEntity, Long> {
}
