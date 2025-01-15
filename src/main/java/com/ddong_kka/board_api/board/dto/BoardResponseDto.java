package com.ddong_kka.board_api.board.dto;


import com.ddong_kka.board_api.board.domain.Board;
import com.ddong_kka.board_api.image.domain.Image;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@ToString
public class BoardResponseDto {
    private Long boardId;
    private String title;
    private String content;
    private int views;
    private List<String> imagePaths;
    private String user;
    private String createdAt;

    @Builder
    public BoardResponseDto(Board board,List<Image> images){
        this.boardId = board.getBoardId();
        this.title = board.getTitle();
        this.content = board.getContent();
        this.views = board.getViews();
        this.createdAt = board.getCreateAt().toString();
        this.user = board.getUser().getEmail();

        // List<Image>에서 imagePath만 추출하여 리스트로 변환
        this.imagePaths = images.stream()
                .map(Image::getImagePath) // Image 객체의 imagePath 필드를 추출
                .collect(Collectors.toList());
    }

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
