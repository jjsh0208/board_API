package com.ddong_kka.board_api.board.repository;

import com.ddong_kka.board_api.board.domain.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {

    @Query("SELECT b FROM Board b WHERE b.boardId NOT IN (SELECT db.board.boardId FROM DeleteBoard db ) ORDER BY b.createAt DESC")
    Page<Board> findActiveBoards(Pageable pageable);



    @Query("SELECT b FROM Board b WHERE b.boardId = :id AND b.boardId NOT IN (SELECT db.board.boardId FROM DeleteBoard db)")
    Optional<Board> findActiveBoardById(@Param("id") Long id);

}
