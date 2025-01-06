package com.ddong_kka.board_api.board.dto;

import com.ddong_kka.board_api.board.domain.Board;
import com.ddong_kka.board_api.user.domain.User;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@ToString
public class BoardListDto {
    private Long boardId;
    private String title;
    private String user;
    private String createdAt;

    @Builder
    public BoardListDto(Board board) {
        this.boardId = board.getBoardId();
        this.title = board.getTitle();
        this.createdAt = String.valueOf(board.getCreateAt());
        this.user = board.getUser().getEmail();
    }
}
