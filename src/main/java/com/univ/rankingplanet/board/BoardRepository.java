package com.univ.rankingplanet.board;

import com.univ.rankingplanet.login.SiteUser;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
    @Override
    Optional<Board> findById(Long id);

    @Override
    List<Board> findAll();

    List<Board> findAllByCategory(String category);



    List<Board> findByBoardTitleContainingOrCategoryContaining(String keyword, String keyword1, Pageable pageable);

    @Transactional
    @Modifying
    @Query("UPDATE Board b SET b.viewCount = b.viewCount + 1 WHERE b.id = :id")
    void incrementViewCount(Long id);

    @Query("SELECT b.likeCount FROM Board b WHERE b.id = :boardId")
    int findLikeCountById(Long boardId);
}
