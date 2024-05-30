package com.univ.rankingplanet.like;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeRecordRepository extends JpaRepository<LikeRecord, Long> {

    List<LikeRecord> findByUserIdAndBoardId(String userId, Long boardId);

    void deleteByUserIdAndBoardId(String userId, Long id);
}
