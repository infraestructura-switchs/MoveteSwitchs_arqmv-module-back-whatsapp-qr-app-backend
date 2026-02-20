package com.restaurante.bot.adapter.out.persistence.repository;

import com.restaurante.bot.adapter.out.persistence.entity.CityEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface CityR2dbcRepository extends ReactiveCrudRepository<CityEntity, Long> {
}
