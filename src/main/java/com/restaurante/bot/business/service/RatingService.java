package com.restaurante.bot.business.service;

import com.restaurante.bot.application.ports.outgoing.RatingRepositoryPort;
import com.restaurante.bot.model.Rating;
import org.springframework.stereotype.Service;
import com.restaurante.bot.application.ports.incoming.RatingUseCase;

import java.util.List;

@Service

public class RatingService implements RatingUseCase {

    private final RatingRepositoryPort ratingRepository;

    public RatingService(RatingRepositoryPort ratingRepository) {
        this.ratingRepository = ratingRepository;
    }


    public List<Rating> ListarRating() {
        return ratingRepository.findAll();
    }

    public Rating guardarRating(Rating rating) {
        return ratingRepository.save(rating);
    }
}
