package com.example.CityPolling.service;

import com.example.CityPolling.dto.CommentResponse;
import com.example.CityPolling.model.Comment;
import com.example.CityPolling.model.Poll;
import com.example.CityPolling.model.User;
import com.example.CityPolling.repository.CommentRepository;
import com.example.CityPolling.repository.PollRepository;
import com.example.CityPolling.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PollRepository pollRepository;
    private final UserRepository userRepository;

    public CommentResponse addComment(Long pollId, String content, String email) {

        Poll poll = pollRepository.findById(pollId)
                .orElseThrow(() -> new RuntimeException("Poll not found"));

        User user = userRepository.findByEmail(email);
        if (user == null) throw new RuntimeException("User not found");

        Comment comment = new Comment();
        comment.setContent(content);
        comment.setPoll(poll);
        comment.setUser(user);

        Comment saved = commentRepository.save(comment);

        return new CommentResponse(
                saved.getId(),
                saved.getContent(),
                user.getUsername(),
                saved.getCreatedAt()
        );
    }

    public List<CommentResponse> getComments(Long pollId) {
        return commentRepository.findByPollIdOrderByCreatedAtDesc(pollId)
                .stream()
                .map(c -> new CommentResponse(
                        c.getId(),
                        c.getContent(),
                        c.getUser().getUsername(),
                        c.getCreatedAt()))
                .toList();
    }

    public void deleteComment(Long commentId, String email) {
        Comment c = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        if (!c.getUser().getEmail().equals(email)) {
            throw new RuntimeException("You can delete only your own comments");
        }

        commentRepository.delete(c);
    }
}