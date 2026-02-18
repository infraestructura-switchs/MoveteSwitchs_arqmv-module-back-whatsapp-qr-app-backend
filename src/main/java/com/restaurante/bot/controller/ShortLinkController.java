package com.restaurante.bot.controller;

import com.restaurante.bot.business.interfaces.ShortLinkService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;


@RestController
@RequestMapping("/${app.request.mapping}/h")
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
public class ShortLinkController {

    private final ShortLinkService service;

    @Value("${landing.page.url}")
    private String landingPageUrl;

    @Value("${app.request.mapping}")
    private String mappingPageUrl;

    public ShortLinkController(ShortLinkService service) {
        this.service = service;
    }

    // Crear ShortLink
    @PostMapping("/create")
    public ResponseEntity<String> createShortLink(@RequestParam String url) {
        var shortLink = service.createShortLink(url);
        String shortUrl = landingPageUrl+"/"+mappingPageUrl +"/h/"+ shortLink.getShortCode();
        return ResponseEntity.ok(shortUrl);
    }

    // Redirigir
    @GetMapping("/{code}")
    public RedirectView redirect(@PathVariable String code) {
        return service.getOriginalUrl(code)
                .map(url -> new RedirectView(url))
                .orElseGet(() -> new RedirectView("/404")); // o lanzar excepción
    }
}

