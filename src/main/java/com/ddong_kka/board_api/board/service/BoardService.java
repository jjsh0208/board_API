package com.ddong_kka.board_api.board.service;

import com.ddong_kka.board_api.Config.JWT.JwtUtil;
import com.ddong_kka.board_api.board.domain.Board;
import com.ddong_kka.board_api.board.dto.BoardResponseDto;
import com.ddong_kka.board_api.board.dto.BoardWriteDto;
import com.ddong_kka.board_api.board.repository.BoardRepository;
import com.ddong_kka.board_api.deleteBoard.domain.DeleteBoard;
import com.ddong_kka.board_api.deleteBoard.repository.DeleteBoardRepository;
import com.ddong_kka.board_api.exception.BoardAlreadyDeletedException;
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

@Service
@AllArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final DeleteBoardRepository deleteBoardRepository;
    private final JwtUtil jwtUtil;

    public Page<BoardResponseDto> getList(int page){

        if (page < 0){
            throw new IllegalArgumentException("페이지 번호는 0 이상이어야 합니다.");
        }

        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Order.desc("createAt")));
        Page<Board> boards = boardRepository.findActiveBoards(pageable);
        return boards.map(BoardResponseDto::new);
    }


    public BoardResponseDto getDetail(Long id) {

        if (id == null){
            throw new IllegalArgumentException("ID는 null일 수 없습니다.");
        }

        Board board =  boardRepository.findActiveBoardById(id)
                .orElseThrow(() -> new BoardNotFoundException("게시글을 찾을 수 없습니다 : ID = " + id));

        board.setViews(board.getViews() + 1);
        boardRepository.save(board);

        BoardResponseDto response = BoardResponseDto.builder()
                .board(board)
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

        if (deleteBoardRepository.existsByBoard(targetBoard)){
            throw new BoardAlreadyDeletedException("이미 삭제된 게시글입니다.");
        }

        DeleteBoard deleteBoard = DeleteBoard.builder()
                .board(targetBoard)
                .build();

        deleteBoardRepository.save(deleteBoard);
    }

}
