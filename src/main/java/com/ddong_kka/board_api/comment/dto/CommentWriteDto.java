package com.ddong_kka.board_api.comment.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@ToString
public class CommentWriteDto {

    @NotBlank(message = "댓글을 입력하십시오.")
    @Size(max = 100 , message = "댓글 내용의 최대 길이는 100 이하입니다.")
    private String content;

}
