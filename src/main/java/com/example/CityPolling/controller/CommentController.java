package com.example.CityPolling.controller;

import com.example.CityPolling.dto.CommentRequest;
import com.example.CityPolling.dto.CommentResponse;
import com.example.CityPolling.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    // Add comment
    @PostMapping("/polls/{pollId}/comments")
    public ResponseEntity<CommentResponse> addComment(@PathVariable Long pollId, @Valid @RequestBody CommentRequest req, Authentication authentication) {
        String email = authentication.getName();
        CommentResponse response = commentService.addComment(pollId, req.getContent(), email);
        return ResponseEntity.ok(response);
    }

    // Get all comments for a poll
    @GetMapping("/polls/{pollId}/comments")
    public ResponseEntity<List<CommentResponse>> getComments(@PathVariable Long pollId) {
        return ResponseEntity.ok(commentService.getComments(pollId));
    }

    // Delete a comment (only your own comment)
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Long commentId, Authentication authentication) {
        String email = authentication.getName();
        commentService.deleteComment(commentId, email);
        return ResponseEntity.ok("Comment deleted");
    }
}