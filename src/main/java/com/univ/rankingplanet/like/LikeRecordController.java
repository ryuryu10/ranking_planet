package com.univ.rankingplanet.like;

import com.univ.rankingplanet.board.Board;
import com.univ.rankingplanet.board.BoardService;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class LikeRecordController {
    private final LikeRecordService likeRecordService;
    private final BoardService boardService;

    @GetMapping("/like/{boardId}/status")
    public ResponseEntity<Map<String, Object>> checkLikeStatus(@PathVariable Long boardId, Authentication authentication) {
        Map<String, Object> response = new HashMap<>();

        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            boolean hasLiked = likeRecordService.isLikedByUser(userDetails.getUsername(), boardId);
            int likeCount = boardService.getLikeCount(boardId);

            response.put("hasLiked", hasLiked);
            response.put("likeCount", likeCount);

            return ResponseEntity.ok().body(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }
}
