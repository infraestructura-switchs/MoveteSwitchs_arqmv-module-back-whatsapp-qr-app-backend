package com.restaurante.bot.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/${app.request.mapping}")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class VersionController {

    @Value("${app.version}")
    private String appVersion;
    @GetMapping("/version")
    public ResponseEntity<String> get() {
        return ResponseEntity.ok(appVersion);
    }
}
