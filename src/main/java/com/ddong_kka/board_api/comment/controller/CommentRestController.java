package com.ddong_kka.board_api.comment.controller;

import com.ddong_kka.board_api.board.controller.BoardRestController;
import com.ddong_kka.board_api.comment.dto.CommentWriteDto;
import com.ddong_kka.board_api.comment.service.CommentService;
import com.ddong_kka.board_api.exception.BoardNotFoundException;
import com.ddong_kka.board_api.exception.CommentNotFoundException;
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
@RequestMapping("api/v1/comment")
@AllArgsConstructor
public class CommentRestController {

    private final CommentService commentService;
    private final Logger logger = LoggerFactory.getLogger(BoardRestController.class);


    @PostMapping("/{id}")
    public ResponseEntity<?> write(@Valid @RequestBody CommentWriteDto commentWriteDto
            , BindingResult bindingResult
            , @PathVariable("id") Long id
            , @RequestHeader("Authorization") String authorizationHeader
    ) {
        String jwtToken = null;
        try {
            if (authorizationHeader != null && !authorizationHeader.startsWith("Bearer ")) {
                throw new IllegalArgumentException("JWT token is missing or invalid");
            }

            jwtToken = authorizationHeader.substring(7);  // 'Bearer ' 부분을 제거하고 JWT만 추출

            if (bindingResult.hasErrors()) {
                List<Map<String, String>> errors = bindingResult.getFieldErrors().stream()
                        .map(fieldError -> {
                            logger.error("{} : {}", fieldError.getField(), fieldError.getDefaultMessage());
                            return Map.of(
                                    "field", fieldError.getField(),
                                    "message", fieldError.getDefaultMessage()
                            );
                        }).toList();
                Map<String, Object> response = Map.of(
                        "status", HttpStatus.BAD_REQUEST.value(),
                        "error", "Validation Field",
                        "message", errors
                );

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            commentService.saveComment(commentWriteDto,id,jwtToken);

            Map<String,Object> successResponse = Map.of(
                    "message", "댓글 작성 성공"
            );

            return ResponseEntity.ok(successResponse);
        } catch(IllegalArgumentException e){
            logger.warn("JWT Token NotFond - {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "status", HttpStatus.UNAUTHORIZED.value(),
                            "error", "Missing JWT Token",
                            "message", e.getMessage()
                    ));
        } catch (Exception e) {
            logger.error("Unexpected Error - {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "status", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "error", "Unexpected Error",
                            "message", "예상치 못한 오류가 발생하였습니다."
                    ));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@Valid @RequestBody CommentWriteDto commentWriteDto
            , BindingResult bindingResult
            , @PathVariable("id") Long id
            , @RequestHeader("Authorization") String authorizationHeader
    ){
        String jwtToken = null;
        try {
            if (authorizationHeader != null && !authorizationHeader.startsWith("Bearer ")) {
                throw new IllegalArgumentException("JWT token is missing or invalid");
            }

            jwtToken = authorizationHeader.substring(7);  // 'Bearer ' 부분을 제거하고 JWT만 추출

            if (bindingResult.hasErrors()) {
                List<Map<String, String>> errors = bindingResult.getFieldErrors().stream()
                        .map(fieldError -> {
                            logger.error("{} : {}", fieldError.getField(), fieldError.getDefaultMessage());
                            return Map.of(
                                    "field", fieldError.getField(),
                                    "message", fieldError.getDefaultMessage()
                            );
                        }).toList();
                Map<String, Object> response = Map.of(
                        "status", HttpStatus.BAD_REQUEST.value(),
                        "error", "Validation Field",
                        "message", errors
                );

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            commentService.updateComment(commentWriteDto,id,jwtToken);

            Map<String,Object> successResponse = Map.of(
                    "message", "댓글 수정 성공"
            );

            return ResponseEntity.ok(successResponse);


        }  catch(IllegalArgumentException e){
            logger.warn("JWT Token NotFond - {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "status", HttpStatus.UNAUTHORIZED.value(),
                            "error", "Missing JWT Token",
                            "message", e.getMessage()
                    ));
        }catch (UserNotFoundException e) {
            logger.warn("User NotFound - {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "status", HttpStatus.NOT_FOUND.value(),
                            "error", "User Not Found",
                            "message", e.getMessage()
                    ));
        } catch (CommentNotFoundException e) {
            logger.warn("Comment NotFound - {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "status", HttpStatus.NOT_FOUND.value(),
                            "error", "Comment Not Found",
                            "message", e.getMessage()
                    ));
        } catch (UnauthorizedAccessException e) {
            logger.warn("Unauthorized Access - {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of(
                            "status", HttpStatus.FORBIDDEN.value(),
                            "error", "Unauthorized Access",
                            "message", e.getMessage()
                    ));
        }
        catch (Exception e) {
            logger.error("Unexpected Error - {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "status", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "error", "Unexpected Error",
                            "message", "예상치 못한 오류가 발생하였습니다."
                    ));
        }
    }

}