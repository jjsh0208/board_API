package com.ddong_kka.board_api.board_api;

import com.ddong_kka.board_api.board.domain.Board;
import com.ddong_kka.board_api.board.repository.BoardRepository;
import com.ddong_kka.board_api.exception.UserNotFoundException;
import com.ddong_kka.board_api.user.domain.User;
import com.ddong_kka.board_api.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BoardApiApplicationTests {

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void contextLoads() {

        User user = userRepository.findById(1L).orElseThrow(()-> new UserNotFoundException("사용자를 찾을 수 없습니다"));

        // 테스트 게시글 100개 생성
        for (int i = 1; i <= 100; i++) {
            Board board = Board.builder()
                    .title("테스트 제목 " + i)
                    .content("테스트 내용입니다. 이 게시글은 자동 생성된 데이터입니다. 게시글 번호: " + i)
                    .user(user)
                    .build();

            boardRepository.save(board);
        }

        System.out.println("100개의 테스트 데이터가 생성되었습니다.");
    }

}
