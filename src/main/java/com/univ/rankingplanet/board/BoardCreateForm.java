package com.univ.rankingplanet.board;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardCreateForm {
    @Size(min = 3, max = 25)
    private String boardTitle;

    private String boardContent;

    private boolean voteFlag;

    private String userId; // 작성자

    private LocalDateTime createdAt;

    private String category;

    private LocalDateTime voteEnd;

    private String voteTitle;

    public boolean getVoteFlag() {
        return this.voteFlag;
    }
}
