package com.univ.rankingplanet.board;

public enum BoardCategory {
    ED("전자제품"),
    IDOL("자동차"),
    SPORTS("컴퓨터"),
    MOVIE("여행"),
    TRIP("아이돌"),
    POLITICS("정치");


    private final String displayName;

    BoardCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
