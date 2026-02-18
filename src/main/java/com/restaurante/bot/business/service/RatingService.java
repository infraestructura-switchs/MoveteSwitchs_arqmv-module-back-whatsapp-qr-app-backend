package com.restaurante.bot.business.service;

import com.restaurante.bot.model.CustomerOrder;
import com.restaurante.bot.model.Rating;
import com.restaurante.bot.repository.RatingRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RatingService  {

    private final RatingRepository ratingRepository;

    public RatingService(RatingRepository ratingRepository) {
        this.ratingRepository = ratingRepository;
    }


    public List<Rating> ListarRating() {
        return ratingRepository.findAll();
    }

    public Rating guardarRating(Rating rating) {
        return ratingRepository.save(rating);
    }
}
