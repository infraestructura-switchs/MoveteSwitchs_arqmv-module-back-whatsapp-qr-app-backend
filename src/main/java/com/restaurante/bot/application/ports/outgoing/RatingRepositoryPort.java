package com.restaurante.bot.application.ports.outgoing;

import com.restaurante.bot.model.Rating;

import java.util.List;

public interface RatingRepositoryPort {
    List<Rating> findAll();

    Rating save(Rating rating);
}