package com.restaurante.bot.repository;

import com.restaurante.bot.model.City;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CityRepository extends JpaRepository<City, Long> {
}
