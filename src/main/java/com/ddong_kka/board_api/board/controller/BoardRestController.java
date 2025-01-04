package com.ddong_kka.board_api.board.controller;

import com.ddong_kka.board_api.board.dto.BoardWriteDto;
import com.ddong_kka.board_api.board.service.BoardService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/v1/board")
@AllArgsConstructor
public class BoardRestController {

    private final BoardService boardService;
    private final Logger logger =  LoggerFactory.getLogger(BoardRestController.class);

    @PostMapping({"/", ""})
    public ResponseEntity<?> write(@Valid @RequestBody BoardWriteDto boardWriteDto
                                    , BindingResult bindingResult
                                    ,@RequestHeader("Authorization") String authorizationHeader){

        String jwtToken = null;
        // Authorization 헤더가 'Bearer <token>' 형식인지 확인
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwtToken = authorizationHeader.substring(7);  // 'Bearer ' 부분을 제거하고 JWT만 추출
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("JWT token is missing or invalid");
        }


        if (bindingResult.hasErrors()){
            List<Map<String,String>> errors = bindingResult.getFieldErrors().stream()
                    .map(fieldError -> {
                        logger.error("{} : {}",fieldError.getField(),fieldError.getDefaultMessage());
                            return Map.of(
                                    "field", fieldError.getField(),
                                    "message", fieldError.getDefaultMessage()
                            );
                    }).toList();
            Map<String,Object> response = Map.of(
                    "status" , HttpStatus.BAD_REQUEST.value(),
                    "error", "Validation Field",
                    "details", errors
            );

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        Long board_id = boardService.saveBoard(boardWriteDto,jwtToken);


        Map<String,Object> successResponse = Map.of(
                "message", "게시글 작성 성공",
                "board_id", board_id
        );

        return ResponseEntity.ok(successResponse);
    }


}
