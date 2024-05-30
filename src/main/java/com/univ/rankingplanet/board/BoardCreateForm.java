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
//    @NotEmpty(message = "사용자ID는 필수항목입니다.")
    private String boardTitle;

//    @NotEmpty(message = "비밀번호는 필수항목입니다.")
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
//    @NotEmpty(message = "비밀번호 확인은 필수항목입니다.")
//    private String password2;

//    @NotEmpty(message = "이메일은 필수항목입니다.")
//    @Email
//    private String email;
}
