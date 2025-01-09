package com.ddong_kka.board_api.comment.dto;

import com.ddong_kka.board_api.comment.domain.Comment;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@ToString
public class CommentListDto {

    private Long commentId;
    private String content;
    private String createAt;
    private String user;


    @Builder
    public CommentListDto(Comment comment){
        this.commentId = comment.getCommentId();
        this.content = comment.getContent();
        this.createAt = String.valueOf(comment.getCreateAt());
        this.user = comment.getUser().getEmail();
    }
}
