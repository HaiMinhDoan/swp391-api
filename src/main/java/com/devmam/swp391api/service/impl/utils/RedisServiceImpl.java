package com.devmam.swp391api.service.impl.utils;

import com.devmam.taraacademyapi.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RedisServiceImpl implements RedisService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    @Override
    public void set(String key, Object value, long ttlSeconds) {
        redisTemplate.opsForValue().set(key, value, Duration.ofSeconds(ttlSeconds));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> clazz) {
        Object obj = redisTemplate.opsForValue().get(key);
        if (obj == null) return null;
        if (clazz.isInstance(obj)) return (T) obj;
        return null;
    }

    @Override
    public boolean delete(String key) {
        Boolean res = redisTemplate.delete(key);
        return res != null && res;
    }

    @Override
    public boolean exists(String key) {
        Boolean res = redisTemplate.hasKey(key);
        return res != null && res;
    }
}


