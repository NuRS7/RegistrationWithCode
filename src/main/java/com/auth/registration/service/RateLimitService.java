package com.auth.registration.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RateLimitService {
    private final RedisTemplate<String, Object> redisTemplate;
    private static final int MAX_ATTEMPTS = 5;
    private static final int TIME_WINDOW_HOURS = 1;

    @Autowired
    public RateLimitService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean tryAcquire(String email) {
        String key = "attempts:" + email;

        // Используем атомарный increment
        Long attempts = redisTemplate.opsForValue().increment(key);

        if (attempts == 1) {
            // Устанавливаем TTL только при первом запросе
            redisTemplate.expire(key, TIME_WINDOW_HOURS, TimeUnit.HOURS);
        }

        return attempts <= MAX_ATTEMPTS;
    }

    public void resetAttempts(String email) {
        redisTemplate.delete("attempts:" + email);
    }
}