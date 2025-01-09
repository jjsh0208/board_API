package com.ddong_kka.board_api.comment.repository;

import com.ddong_kka.board_api.board.domain.Board;
import com.ddong_kka.board_api.comment.domain.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment , Long> {

    Page<Comment> findAllByBoard(Board board, Pageable pageable);
}
