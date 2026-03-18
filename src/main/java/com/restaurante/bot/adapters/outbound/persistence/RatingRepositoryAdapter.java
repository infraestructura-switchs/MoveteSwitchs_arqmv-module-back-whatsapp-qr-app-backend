package com.restaurante.bot.adapters.outbound.persistence;

import com.restaurante.bot.application.ports.outgoing.RatingRepositoryPort;
import com.restaurante.bot.model.Rating;
import com.restaurante.bot.repository.RatingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RatingRepositoryAdapter implements RatingRepositoryPort {

    private final RatingRepository ratingRepository;

    @Override
    public List<Rating> findAll() {
        return ratingRepository.findAll();
    }

    @Override
    public Rating save(Rating rating) {
        return ratingRepository.save(rating);
    }
}