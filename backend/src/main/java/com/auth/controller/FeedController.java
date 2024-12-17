package com.auth.controller;

import com.auth.model.UserFeed;
import com.auth.service.FeedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/feed")
public class FeedController {
    @Autowired
    private FeedService feedService;

    @GetMapping
    public ResponseEntity<Page<UserFeed>> getFeed(
            @RequestAttribute("userId") Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(feedService.getUserFeed(userId, PageRequest.of(page, size)));
    }
}
