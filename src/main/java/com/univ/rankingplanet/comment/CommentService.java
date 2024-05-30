package com.univ.rankingplanet.comment;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;

    public void addComment(CommentCreateForm commentCreateForm) {
        Comment comment = new Comment();
        comment.setUserId(commentCreateForm.getUserId());
        comment.setContent(commentCreateForm.getContent());
        comment.setCreatedAt(LocalDateTime.now());
        comment.setBoardId(commentCreateForm.getBoardId());
        commentRepository.save(comment);
    }
}
