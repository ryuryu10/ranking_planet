package com.univ.rankingplanet.vote;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class VoteService {
    private final VoteRepository voteRepository;
    private final VoteRecordRepository voteRecordRepository;
    public void saveVote(Long boardId, int voteNumber, String text, String filePath, String originalFileName, Long voteCount){
        Vote vote = new Vote();

        vote.setBoardId(boardId); // boardId를 이용하여 Board 객체 생성
        vote.setVoteNumber(voteNumber);
        vote.setText(text);
        vote.setImagePath(filePath.toString()); // 이미지 파일 경로 저장
        vote.setImageName(originalFileName); // 이미지 원래 이름 저장
        vote.setVoteCount(0L);

        voteRepository.save(vote);
    }

    public void vote(int voteNumber, Long boardId, String userId) {
        Vote vote = voteRepository.findByBoardIdAndVoteNumber(boardId, voteNumber);
        boolean hasVoted = voteRecordRepository.existsByBoardIdAndVoteNumberAndUserId(boardId, voteNumber, userId);
        System.out.println(hasVoted);
            if (!hasVoted) {
                // 투표 기록 생성 및 저장
                VoteRecord voteRecord = new VoteRecord();
                voteRecord.setBoardId(boardId);
                voteRecord.setVoteNumber(voteNumber);
                // 사용자 ID 설정
                voteRecord.setUserId(userId); // 사용자 엔티티에 맞게 설정 필요
                voteRecordRepository.save(voteRecord);

                // 투표수 증가
                vote.setVoteCount(vote.getVoteCount() + 1);
                voteRepository.save(vote);
            } else {
                // 이미 투표한 경우 처리할 로직 추가
            }
//        }
    }


    public List<Vote> getAllVotes(Long boardId) {
        List<Vote> voteList = voteRepository.findByBoardIdOrderByVoteNumber(boardId);
        return voteList;
    }

    public boolean isVoteRecordExists(Long boardId, String userId) {
        return voteRecordRepository.existsByBoardIdAndUserId(boardId, userId);
    }

    @Transactional
    public void updateVote(Long boardId, Long voteId, String text, String imagePath, String imageName) {
        Vote vote = voteRepository.findByBoardIdAndId(boardId, voteId);
        vote.setText(text);
        vote.setImagePath(imagePath);
        vote.setImageName(imageName);
        voteRepository.save(vote);
    }
}
