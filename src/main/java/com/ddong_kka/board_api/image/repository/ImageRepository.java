package com.ddong_kka.board_api.image.repository;

import com.ddong_kka.board_api.board.domain.Board;
import com.ddong_kka.board_api.image.domain.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image,Long> {

    // 특정 boardId에 연결된 모든 이미지를 조회
    List<Image> findAllByBoard(Board board);
}
