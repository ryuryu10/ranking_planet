package com.univ.rankingplanet.board;

public enum BoardCategory {
    NOTICE("공지사항"),
    FREE("자유게시판"),
    QNA("질문과답변"),
    REVIEW("리뷰게시판");

    private final String displayName;

    BoardCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
