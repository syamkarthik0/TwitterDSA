package com.auth.controller;

import com.auth.cache.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/cache")
@CrossOrigin(origins = "http://localhost:3000")
public class CacheController {

    @Autowired
    private CacheService cacheService;

    @GetMapping("/stats")
    public Map<String, Object> getCacheStats() {
        return cacheService.getCacheStats();
    }
}
