package com.univ.rankingplanet.like;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikeRecordService {
    private final LikeRecordRepository likeRecordRepository;

    public boolean isLikedByUser(String userId, Long boardId) {
        int size = likeRecordRepository.findByUserIdAndBoardId(userId, boardId).size();
        return size == 1 ? true : false;
    }

}
