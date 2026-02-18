package com.restaurante.bot.business.service;

import com.restaurante.bot.business.interfaces.ShortLinkService;
import com.restaurante.bot.model.ShortLink;
import com.restaurante.bot.repository.ShortLinkRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Random;

@Service
public class ShortLinkServiceImpl implements ShortLinkService {

    private final ShortLinkRepository repository;
    private final Random random = new Random();

    public ShortLinkServiceImpl(ShortLinkRepository repository) {
        this.repository = repository;
    }

    @Transactional
    @Override
    public ShortLink createShortLink(String originalUrl) {
        String code = generateCode(6);

        ShortLink shortLink = new ShortLink();
        shortLink.setOriginalUrl(originalUrl);
        shortLink.setShortCode(code);

        return repository.save(shortLink);
    }

    @Override
    public Optional<String> getOriginalUrl(String code) {
        return repository.findByShortCode(code)
                .map(ShortLink::getOriginalUrl);
    }

    private String generateCode(int length) {
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}
