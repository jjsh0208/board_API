package com.ddong_kka.board_api.board.service;

import com.ddong_kka.board_api.Config.JWT.JwtUtil;
import com.ddong_kka.board_api.board.domain.Board;
import com.ddong_kka.board_api.board.dto.BoardDetailDto;
import com.ddong_kka.board_api.board.dto.BoardListDto;
import com.ddong_kka.board_api.board.dto.BoardWriteDto;
import com.ddong_kka.board_api.board.repository.BoardRepository;
import com.ddong_kka.board_api.exception.BoardNotFoundException;
import com.ddong_kka.board_api.exception.UnauthorizedAccessException;
import com.ddong_kka.board_api.exception.UserNotFoundException;
import com.ddong_kka.board_api.user.domain.User;
import com.ddong_kka.board_api.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public Page<BoardListDto> getList(int page){

        if (page < 0){
            throw new IllegalArgumentException("페이지 번호는 0 이상이어야 합니다.");
        }

        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createAt"));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        Page<Board> boards = boardRepository.findAll(pageable);
        return boards.map(BoardListDto::new);
    }


    public BoardDetailDto getDetail(Long id) {

        if (id == null){
            throw new IllegalArgumentException("ID는 null일 수 없습니다.");
        }


        Board board =  boardRepository.findById(id)
                .orElseThrow(() -> new BoardNotFoundException("게시글을 찾을 수 없습니다 : ID = " + id));

        BoardDetailDto response = BoardDetailDto.builder()
                .title(board.getTitle())
                .content(board.getContent())
                .user(board.getUser())
                .build();

        return response;
    }

    public Long saveBoard(BoardWriteDto boardWriteDto,String jwtToken){
        String userEmail = jwtUtil.getEmail(jwtToken);

        User user = userRepository.findByEmail(userEmail).orElseThrow(()-> new UserNotFoundException("사용자를 찾을 수 없습니다 : "+ userEmail));

        Board board = Board.builder()
                .title(boardWriteDto.getTitle())
                .content(boardWriteDto.getContent())
                .user(user)
                .build();

        return boardRepository.save(board).getBoardId();
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

        return targetBoard.getBoardId();
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
