package com.restaurante.bot.application.ports.incoming;

import com.restaurante.bot.model.Rating;
import java.util.List;

public interface RatingUseCase {
    List<Rating> ListarRating();
    Rating guardarRating(Rating rating);
}