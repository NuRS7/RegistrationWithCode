package com.auth.registration.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RegistrationCacheService {
    private final RedisTemplate<String, Object> redisTemplate;
    private static final int REGISTRATION_TIMEOUT_MINUTES = 15;

    @Autowired
    public RegistrationCacheService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void saveTemporaryUser(String email, String password) {
        redisTemplate.opsForValue().set("reg:" + email, password,
                REGISTRATION_TIMEOUT_MINUTES, TimeUnit.MINUTES);
    }

    public String getTemporaryUser(String email) {
        return (String) redisTemplate.opsForValue().get("reg:" + email);
    }

    public void removeTemporaryUser(String email) {
        redisTemplate.delete("reg:" + email);
    }

    public boolean hasTemporaryUser(String email) {
        return redisTemplate.hasKey("reg:" + email);
    }
}