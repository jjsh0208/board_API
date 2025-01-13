package com.ddong_kka.board_api.board.dto;


import com.ddong_kka.board_api.board.domain.Board;
import lombok.*;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@ToString
public class BoardResponseDto {
    private Long boardId;
    private String title;
    private String content;
    private int views;
    private String user;
    private String createdAt;

    @Builder
    public BoardResponseDto(Board board){
        this.boardId = board.getBoardId();
        this.title = board.getTitle();
        this.content = board.getContent();
        this.views = board.getViews();
        this.createdAt = board.getCreateAt().toString();
        this.user = board.getUser().getEmail();

    }

}
