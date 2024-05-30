package com.univ.rankingplanet.vote;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
    List<Vote> findByBoardIdOrderByVoteNumber(Long boardId);

    Vote findByBoardIdAndVoteNumber(Long boardId, int voteNumber);

    Vote findByBoardIdAndId(Long boardId, Long voteId);
}
