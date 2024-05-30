package com.univ.rankingplanet.vote;

import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VoteRecordRepository extends JpaRepository<VoteRecord, Long> {

    @Query("SELECT CASE WHEN COUNT(vr) > 0 THEN TRUE ELSE FALSE END " +
            "FROM VoteRecord vr " +
            "WHERE vr.boardId = :boardId " +
            "AND vr.voteNumber = :voteNumber " +
            "AND vr.userId = :userId")
    boolean existsByBoardIdAndVoteNumberAndUserId(@Param("boardId") Long boardId,
                                                  @Param("voteNumber") int voteNumber,
                                                  @Param("userId") String userId);
    List<VoteRecord> findVoteNumbersByUserIdAndBoardId(@Param("userId") String userId,
                                                    @Param("boardId") Long boardId);

    boolean existsByBoardIdAndUserId(Long boardId, String userId);

    @Transactional
    @Modifying
    @Query("UPDATE VoteRecord v SET v.voteNumber = :voteNumber WHERE v.boardId = :boardId AND v.userId = :userId")
    int updateVoteNumberByBoardIdAndUserId(@Param("voteNumber") int voteNumber, @Param("boardId") Long boardId, @Param("userId") String userId);

    VoteRecord findByBoardIdAndUserId(Long boardId, String userId);
}
