package com.ddong_kka.board_api.board.dto;

import com.ddong_kka.board_api.user.domain.User;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@ToString
public class BoardDetailDto {

    private String title;

    private String content;

    private String user;

    @Builder
    public BoardDetailDto(String title, String content, User user) {
        this.title = title;
        this.content = content;
        this.user = user.getEmail();
    }
}


