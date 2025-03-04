package com.ddong_kka.board_api.board.service;

import com.ddong_kka.board_api.Config.JWT.JwtUtil;
import com.ddong_kka.board_api.board.domain.Board;
import com.ddong_kka.board_api.board.dto.BoardResponseDto;
import com.ddong_kka.board_api.board.dto.BoardWriteDto;
import com.ddong_kka.board_api.board.repository.BoardRepository;
import com.ddong_kka.board_api.exception.*;
import com.ddong_kka.board_api.image.domain.Image;
import com.ddong_kka.board_api.image.repository.ImageRepository;
import com.ddong_kka.board_api.image.service.ImageService;
import com.ddong_kka.board_api.user.domain.User;
import com.ddong_kka.board_api.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@AllArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final ImageRepository imageRepository;
    private final ImageService imageService;
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

        List<Image> images = imageRepository.findAllByBoard(board);

        board.setViews(board.getViews() + 1);
        boardRepository.save(board);

        BoardResponseDto response = BoardResponseDto.builder()
                .board(board)
                .images(images)
                .build();

        return response;
    }

    @Transactional
    public Long saveBoard(BoardWriteDto boardWriteDto, List<MultipartFile> imageFiles, String jwtToken){
        String userEmail = jwtUtil.getEmail(jwtToken);

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(()-> new UserNotFoundException("사용자를 찾을 수 없습니다 : "+ userEmail));

        Board board = Board.builder()
                .title(boardWriteDto.getTitle())
                .content(boardWriteDto.getContent())
                .user(user)
                .build();

        boardRepository.save(board);

        // 이미지 파일 처리
        if (imageFiles != null && !imageFiles.isEmpty()) {
            for (MultipartFile imageFile : imageFiles) {
                try {
                    // 이미지 파일 저장 및 경로 반환
                    String imagePath = imageService.saveFile(imageFile);
                    String originalFileName = imageFile.getOriginalFilename();
                    String imageSize = String.valueOf(imageFile.getSize());

                    // Image 엔티티 생성 및 저장
                    Image image = Image.builder()
                            .originName(originalFileName)
                            .saveName(imagePath)
                            .imagePath(imagePath)
                            .imageSize(imageSize)
                            .board(board)
                            .build();

                    imageRepository.save(image);
                } catch (IOException e) {
                    throw new ImageSaveException("이미지 저장 중 오류가 발생했습니다: " + imageFile.getOriginalFilename());
                }
            }
        }

        return board.getBoardId();
    }

    public Long updateBoard(BoardWriteDto boardWriteDto, Long id, String jwtToken) {
        String userEmail = jwtUtil.getEmail(jwtToken);

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(()-> new UserNotFoundException("사용자를 찾을 수 없습니다 : "+ userEmail));

        Board targetBoard = boardRepository.findById(id)
                .orElseThrow(() -> new BoardNotFoundException("게시글을 찾을 수 없습니다 : ID = " + id));

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

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(()-> new UserNotFoundException("사용자를 찾을 수 없습니다 : "+ userEmail));

        Board targetBoard = boardRepository.findById(id)
                .orElseThrow(() -> new BoardNotFoundException("게시글을 찾을 수 없습니다 : ID = " + id));

        if (!targetBoard.getUser().equals(user)){
            throw new UnauthorizedAccessException("권한이 없습니다.");
        }

        targetBoard.setIsDeleted(true);

        boardRepository.save(targetBoard);
    }

}
