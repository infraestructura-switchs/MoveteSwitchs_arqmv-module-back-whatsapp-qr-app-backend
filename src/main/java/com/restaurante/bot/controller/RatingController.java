package com.restaurante.bot.controller;

import com.restaurante.bot.model.Rating;
import com.restaurante.bot.business.service.RatingService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/${app.request.mapping}/rating")
@CrossOrigin(origins = "*", methods= {RequestMethod.GET,RequestMethod.POST,RequestMethod.PUT,RequestMethod.DELETE})
public class RatingController {
    private final RatingService ratingService;

    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @GetMapping
    public List<Rating> ListarRating() {
        return ratingService.ListarRating();
    }

    @PostMapping
    public Rating guardarRating(@RequestBody Rating rating) {
        return ratingService.guardarRating(rating);
    }
}
