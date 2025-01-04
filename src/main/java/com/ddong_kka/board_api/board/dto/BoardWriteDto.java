package com.ddong_kka.board_api.board.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@ToString
public class BoardWriteDto {

    @NotBlank(message = "제목을 입력하십시오.")
    @Size(max = 30 , message =  "제목의 길이는 최대 30이하 입니다.")
    private String title;

    @NotBlank(message = "내용을 입력하십시오.")
    @Size(max = 500, message = "게시글 내용의 최대 길이는 500이하입니다.")
    private String content;

}
