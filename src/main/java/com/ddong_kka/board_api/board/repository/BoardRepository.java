package com.ddong_kka.board_api.board.repository;

import com.ddong_kka.board_api.board.domain.Board;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Long> {
}
