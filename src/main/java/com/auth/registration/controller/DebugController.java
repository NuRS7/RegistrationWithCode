package com.auth.registration.controller;

import com.auth.registration.service.RateLimitService;
import com.auth.registration.service.RegistrationCacheService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/debug")
@Tag(name = "Проверка кэширование", description = "Проверям с помощью debugging кэшинг в redis")
public class DebugController {

    private final RegistrationCacheService cacheService;
    private final RateLimitService rateLimitService;

    public DebugController(RegistrationCacheService cacheService,
                           RateLimitService rateLimitService) {
        this.cacheService = cacheService;
        this.rateLimitService = rateLimitService;
    }

    @GetMapping("/test-redis")
    public String testRedis() {
        String email = "test@example.com";

        // Test cache
        cacheService.saveTemporaryUser(email, "testPassword");
        String cached = cacheService.getTemporaryUser(email);

        // Test rate limit
        boolean allowed = rateLimitService.tryAcquire(email);

        return "Cached: " + cached + ", Rate limit allowed: " + allowed;
    }
}