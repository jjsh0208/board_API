package com.ddong_kka.board_api.board.service;

import com.ddong_kka.board_api.Config.JWT.JwtUtil;
import com.ddong_kka.board_api.board.domain.Board;
import com.ddong_kka.board_api.board.dto.BoardWriteDto;
import com.ddong_kka.board_api.board.repository.BoardRepository;
import com.ddong_kka.board_api.exception.BoardNotFoundException;
import com.ddong_kka.board_api.exception.UnauthorizedAccessException;
import com.ddong_kka.board_api.exception.UserNotFoundException;
import com.ddong_kka.board_api.user.domain.User;
import com.ddong_kka.board_api.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public Long saveBoard(BoardWriteDto boardWriteDto,String jwtToken){
        String userEmail = jwtUtil.getEmail(jwtToken);

        User user = userRepository.findByEmail(userEmail).orElseThrow(()-> new UserNotFoundException("사용자를 찾을 수 없습니다 : "+ userEmail));

        Board board = Board.builder()
                .title(boardWriteDto.getTitle())
                .content(boardWriteDto.getContent())
                .user(user)
                .build();

        return boardRepository.save(board).getBoard_id();
    }

    public Long updateBoard(BoardWriteDto boardWriteDto, Long id, String jwtToken) {
        String userEmail = jwtUtil.getEmail(jwtToken);

        User user = userRepository.findByEmail(userEmail).orElseThrow(()-> new UserNotFoundException("사용자를 찾을 수 없습니다 : "+ userEmail));

        Board targetBoard = boardRepository.findById(id).orElseThrow(() -> new BoardNotFoundException("게시글을 찾을 수 없습니다 : ID = " + id));

        if (!targetBoard.getUser().equals(user)){
            throw new UnauthorizedAccessException("권한이 없습니다.");
        }

        targetBoard.setTitle(boardWriteDto.getTitle());
        targetBoard.setContent(boardWriteDto.getContent());
        boardRepository.save(targetBoard);

        return targetBoard.getBoard_id();
    }

    public void deleteBoard(Long id, String jwtToken) {

        String userEmail = jwtUtil.getEmail(jwtToken);

        User user = userRepository.findByEmail(userEmail).orElseThrow(()-> new UserNotFoundException("사용자를 찾을 수 없습니다 : "+ userEmail));

        Board targetBoard = boardRepository.findById(id).orElseThrow(() -> new BoardNotFoundException("게시글을 찾을 수 없습니다 : ID = " + id));

        if (!targetBoard.getUser().equals(user)){
            throw new UnauthorizedAccessException("권한이 없습니다.");
        }

        boardRepository.delete(targetBoard);

    }
}
