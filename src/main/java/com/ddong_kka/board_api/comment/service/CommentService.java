package com.ddong_kka.board_api.comment.service;

import com.ddong_kka.board_api.Config.JWT.JwtUtil;
import com.ddong_kka.board_api.board.domain.Board;
import com.ddong_kka.board_api.board.repository.BoardRepository;
import com.ddong_kka.board_api.comment.domain.Comment;
import com.ddong_kka.board_api.comment.dto.CommentWriteDto;
import com.ddong_kka.board_api.comment.repository.CommentRepository;
import com.ddong_kka.board_api.exception.BoardNotFoundException;
import com.ddong_kka.board_api.exception.CommentNotFoundException;
import com.ddong_kka.board_api.exception.UnauthorizedAccessException;
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
        // create에서 사용하는 id는 게시글의 id 이다.
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

    public void updateComment(CommentWriteDto commentWriteDto, Long id, String jwtToken) {
        // update에서 사용하는 id는 댓글의 id 이다.

        String userEmail = jwtUtil.getEmail(jwtToken);

        User user = userRepository.findByEmail(userEmail).orElseThrow(()-> new UserNotFoundException("사용자를 찾을 수 없습니다 : "+ userEmail));

        Comment targetComment = commentRepository.findById(id).orElseThrow(() -> new CommentNotFoundException("댓글을 찾을 수 없습니다 : ID = " + id));

        if (!targetComment.getUser().equals(user)){
            throw new UnauthorizedAccessException("권한이 없습니다.");
        }


        targetComment.setContent(commentWriteDto.getContent());
        commentRepository.save(targetComment);

    }
}
