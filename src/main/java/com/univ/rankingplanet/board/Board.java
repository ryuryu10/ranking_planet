package com.univ.rankingplanet.board;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "board")
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="board_title")
    private String boardTitle;

    @Column(name = "board_content", columnDefinition = "TEXT")
    private String boardContent;

    private String userId;

    private String category;

    private LocalDateTime createdAt;

    @Column(name = "vote_start")
    private LocalDateTime voteStart;

    @Column(name = "vote_end")
    private LocalDateTime voteEnd;

    @Column(name = "vote_flag")
    private boolean voteFlag;
    @Column(name = "view_count", nullable = false, columnDefinition = "int default 0")
    private int viewCount = 0;

    private String voteTitle;

    @Column(name = "like_count", nullable = false, columnDefinition = "int default 0")
    private int likeCount = 0;


    public String getVoteTitle() {
        return voteTitle;
    }

    public void setVoteTitle(String voteTitle) {
        this.voteTitle = voteTitle;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBoardTitle() {
        return boardTitle;
    }

    public void setBoardTitle(String boardTitle) {
        this.boardTitle = boardTitle;
    }

    public String getBoardContent() {
        return boardContent;
    }

    public void setBoardContent(String boardContent) {
        this.boardContent = boardContent;
    }

    public boolean getVoteFlag() {
        return voteFlag;
    }

    public void setVoteFlag(boolean voteFlag) {
        this.voteFlag = voteFlag;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public LocalDateTime getVoteStart() {
        return voteStart;
    }

    public void setVoteStart(LocalDateTime voteStart) {
        this.voteStart = voteStart;
    }

    public LocalDateTime getVoteEnd() {
        return voteEnd;
    }

    public void setVoteEnd(LocalDateTime voteEnd) {
        this.voteEnd = voteEnd;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    //    public boolean getVoteFlag() {
//        return voteFlag;
//    }
//
//    public void setVoteFlag(boolean voteFlag) {
//        this.voteFlag = voteFlag;
//    }
}
