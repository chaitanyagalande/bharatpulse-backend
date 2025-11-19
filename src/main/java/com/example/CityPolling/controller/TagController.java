package com.example.CityPolling.controller;

import com.example.CityPolling.model.Tag;
import com.example.CityPolling.model.User;
import com.example.CityPolling.service.TagService;
import com.example.CityPolling.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
public class TagController {
    private final UserService userService;
    private final TagService tagService;

    public TagController(UserService userService, TagService tagService) {
        this.userService = userService;
        this.tagService = tagService;
    }

    // Get Popular Tags so they can be shown below Search Box in frontend
    @GetMapping("/popular")
    public ResponseEntity<?> getPopularTags(Authentication authentication) {
        String email = authentication.getName();
        User user = userService.findByEmail(email);

        List<Tag> tags = tagService.findByCityOrderByUsageCountDesc(user.getCity());

        return ResponseEntity.ok(tags);
    }

}
