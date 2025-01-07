package com.ddong_kka.board_api.comment.service;

import com.ddong_kka.board_api.Config.JWT.JwtUtil;
import com.ddong_kka.board_api.board.domain.Board;
import com.ddong_kka.board_api.board.repository.BoardRepository;
import com.ddong_kka.board_api.comment.domain.Comment;
import com.ddong_kka.board_api.comment.dto.CommentWriteDto;
import com.ddong_kka.board_api.comment.repository.CommentRepository;
import com.ddong_kka.board_api.exception.BoardNotFoundException;
import com.ddong_kka.board_api.exception.UserNotFoundException;
import com.ddong_kka.board_api.user.domain.User;
import com.ddong_kka.board_api.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public void saveComment(CommentWriteDto commentWriteDto, Long id, String jwtToken) {
        String userEmail = jwtUtil.getEmail(jwtToken);

        User user = userRepository.findByEmail(userEmail).orElseThrow(()-> new UserNotFoundException("사용자를 찾을 수 없습니다 : "+ userEmail));

        Board targetBoard = boardRepository.findById(id).orElseThrow(() -> new BoardNotFoundException("게시글을 찾을 수 없습니다 : ID = " + id));

        Comment comment = Comment.builder()
                .content(commentWriteDto.getContent())
                .board(targetBoard)
                .user(user)
                .build();

        commentRepository.save(comment);
    }
}
