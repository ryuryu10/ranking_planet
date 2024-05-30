package com.univ.rankingplanet.vote;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RequiredArgsConstructor
public class VoteController {

    private final VoteService voteService;

    private final HttpSession httpSession;

    private final VoteRecordRepository voteRecordRepository;

    private final VoteRepository voteRepository;

    @PostMapping("/api/vote")
    public String vote(@RequestParam int voteNumber,  @RequestParam Long boardId, @RequestParam String userId) {
        VoteRecord previousVoteRecord = voteRecordRepository.findByBoardIdAndUserId(boardId, userId);
        if(previousVoteRecord != null){
            Vote previousVote = voteRepository.findByBoardIdAndVoteNumber(boardId, previousVoteRecord.getVoteNumber());
            previousVote.setVoteCount(previousVote.getVoteCount() - 1);
            voteRepository.save(previousVote);
        }
        // 새로 투표한 항목의 voteCount를 증가시킴
        Vote newVote = voteRepository.findByBoardIdAndVoteNumber(boardId, voteNumber);
        newVote.setVoteCount(newVote.getVoteCount() + 1);
        voteRepository.save(newVote);

        // 투표 기록을 업데이트
        if (previousVoteRecord != null) {
            previousVoteRecord.setVoteNumber(voteNumber);
            voteRecordRepository.save(previousVoteRecord);
        } else {
            VoteRecord voteRecord = new VoteRecord();
            voteRecord.setBoardId(boardId);
            voteRecord.setUserId(userId);
            voteRecord.setVoteNumber(voteNumber);
            voteRecordRepository.save(voteRecord);
        }
        return "redirect:/board/detail/" + boardId;
    }
}
