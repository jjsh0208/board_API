package com.ddong_kka.board_api.comment.repository;

import com.ddong_kka.board_api.comment.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment , Long> {
}
