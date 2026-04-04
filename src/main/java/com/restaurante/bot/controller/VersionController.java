package com.restaurante.bot.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthComponent;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.health.HttpCodeStatusMapper;
import org.springframework.boot.actuate.health.Status;
import org.springframework.boot.actuate.health.SystemHealth;
import org.springframework.boot.info.BuildProperties;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/${app.request.mapping}")
@RequiredArgsConstructor
@Slf4j
public class VersionController {

    private final BuildProperties buildProperties;
    private final HealthEndpoint healthEndpoint;
    private final HttpCodeStatusMapper httpCodeStatusMapper;

    @GetMapping("/version")
    public ResponseEntity<String> get() {
        return ResponseEntity.ok(buildProperties.getVersion());
    }

    @GetMapping({"/status", "/health"})
    public ResponseEntity<Map<String, Object>> status() {
        HealthComponent healthComponent = healthEndpoint.health();
        Status overallStatus = extractStatus(healthComponent);
        HttpStatusCode httpStatus = HttpStatusCode.valueOf(httpCodeStatusMapper.getStatusCode(overallStatus));

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", overallStatus.getCode());
        body.put("version", buildProperties.getVersion());
        body.put("timestamp", OffsetDateTime.now());

        Map<String, String> componentStatuses = new LinkedHashMap<>();
        if (healthComponent instanceof SystemHealth systemHealth) {
            systemHealth.getComponents().forEach((name, component) ->
                    componentStatuses.put(name, extractStatus(component).getCode()));
        }

        body.put("database", componentStatuses.getOrDefault("db", overallStatus.getCode()));
        body.put("components", componentStatuses);

        return ResponseEntity.status(httpStatus).body(body);
    }

    private Status extractStatus(HealthComponent healthComponent) {
        if (healthComponent instanceof SystemHealth systemHealth) {
            return systemHealth.getStatus();
        }
        if (healthComponent instanceof Health health) {
            return health.getStatus();
        }
        return Status.UNKNOWN;
    }
}
