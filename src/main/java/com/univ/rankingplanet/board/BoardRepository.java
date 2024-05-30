package com.univ.rankingplanet.board;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query("SELECT b FROM Board b WHERE b.category = :category " +
            "ORDER BY CASE " +
            "WHEN :criteria = 'likeCount' THEN b.likeCount " +
            "WHEN :criteria = 'viewCount' THEN b.viewCount END DESC")
    List<Board> findBoardsByCategoryAndOrderByCriteria(@Param("criteria") String criteria, @Param("category") String category);

    @Query("SELECT b FROM Board b " +
            "ORDER BY CASE " +
            "WHEN :criteria = 'likeCount' THEN b.likeCount " +
            "WHEN :criteria = 'viewCount' THEN b.viewCount END DESC")
    List<Board> findAllOrderByCriteria(String criteria);

    List<Board> findBoardsByCategory(String category);

    @Query("SELECT b FROM Board b " +
            "ORDER BY b.id DESC")
    List<Board> findBoardsOrderById();

}
