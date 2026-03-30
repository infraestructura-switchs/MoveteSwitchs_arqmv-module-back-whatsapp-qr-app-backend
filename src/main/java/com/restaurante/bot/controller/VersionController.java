package com.restaurante.bot.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.info.BuildProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/${app.request.mapping}")
@RequiredArgsConstructor
@Slf4j
public class VersionController {

    private final BuildProperties buildProperties;

    @GetMapping("/version")
    public ResponseEntity<String> get() {
        return ResponseEntity.ok(buildProperties.getVersion());
    }
}
