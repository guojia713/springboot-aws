package com.example.app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HealthController {

    // AWS Elastic Beanstalk polls this endpoint to check if your app is alive.
    // Must return HTTP 200 — if it doesn't, Beanstalk will restart your instance.
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "springboot-aws"
        ));
    }
}
