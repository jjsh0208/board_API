package com.ddong_kka.board_api.board.controller;

import com.ddong_kka.board_api.board.dto.BoardWriteDto;
import com.ddong_kka.board_api.board.service.BoardService;
import com.ddong_kka.board_api.exception.BoardNotFoundException;
import com.ddong_kka.board_api.exception.UnauthorizedAccessException;
import com.ddong_kka.board_api.exception.UserNotFoundException;
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
        try{
            if (authorizationHeader != null && !authorizationHeader.startsWith("Bearer ")) {
                throw new IllegalArgumentException("JWT token is missing or invalid");
            }

            jwtToken = authorizationHeader.substring(7);  // 'Bearer ' 부분을 제거하고 JWT만 추출

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
        }  catch(IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "status", HttpStatus.UNAUTHORIZED.value(),
                            "error", "User Not Found",
                            "message", e.getMessage()
                    ));
        }  catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "status", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "error", "Unexpected Error",
                            "message", "예상치 못한 오류가 발생하였습니다."
                    ));
        }

    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@Valid @RequestBody BoardWriteDto boardWriteDto
            , BindingResult bindingResult
            ,@RequestHeader("Authorization") String authorizationHeader, @PathVariable("id") Long id) {

        String jwtToken;
        try{
            if (authorizationHeader != null && !authorizationHeader.startsWith("Bearer ")) {
                throw new IllegalArgumentException("JWT token is missing or invalid");
            }

            jwtToken = authorizationHeader.substring(7);  // 'Bearer ' 부분을 제거하고 JWT만 추출

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

            Long board_id = boardService.updateBoard(boardWriteDto,id,jwtToken);

            Map<String,Object> successResponse = Map.of(
                    "message", "게시글 수정 성공",
                    "board_id", board_id
            );

            return ResponseEntity.ok(successResponse);
        } catch(IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "status", HttpStatus.UNAUTHORIZED.value(),
                            "error", "User Not Found",
                            "message", e.getMessage()
                    ));
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "status", HttpStatus.NOT_FOUND.value(),
                            "error", "User Not Found",
                            "message", e.getMessage()
                    ));
        } catch (BoardNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "status", HttpStatus.NOT_FOUND.value(),
                            "error", "Board Not Found",
                            "message", e.getMessage()
                    ));
        } catch (UnauthorizedAccessException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of(
                            "status", HttpStatus.FORBIDDEN.value(),
                            "error", "Unauthorized Access",
                            "message", e.getMessage()
                    ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "status", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "error", "Unexpected Error",
                            "message", "예상치 못한 오류가 발생하였습니다."
                    ));
        }
    }
}
