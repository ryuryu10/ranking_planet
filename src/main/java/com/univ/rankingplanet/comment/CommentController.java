package com.univ.rankingplanet.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping(value="/api/comment")
    public ResponseEntity<String> addComment(@RequestBody CommentCreateForm commentCreateForm) {
        commentService.addComment(commentCreateForm);
        return ResponseEntity.ok("댓글이 추가되었습니다.");
    }

}
