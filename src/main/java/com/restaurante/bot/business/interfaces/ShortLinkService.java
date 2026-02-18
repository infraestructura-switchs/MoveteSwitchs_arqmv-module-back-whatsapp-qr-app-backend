package com.restaurante.bot.business.interfaces;

import com.restaurante.bot.model.ShortLink;
import com.restaurante.bot.repository.ShortLinkRepository;

import java.util.Optional;

public interface ShortLinkService {

    public ShortLink createShortLink(String originalUrl);

    public Optional<String> getOriginalUrl(String code) ;
}