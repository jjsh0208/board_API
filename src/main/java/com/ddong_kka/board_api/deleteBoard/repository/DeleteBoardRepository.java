package com.ddong_kka.board_api.deleteBoard.repository;

import com.ddong_kka.board_api.board.domain.Board;
import com.ddong_kka.board_api.deleteBoard.domain.DeleteBoard;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeleteBoardRepository extends JpaRepository<DeleteBoard,Long> {

    boolean existsByBoard(Board board);


}
