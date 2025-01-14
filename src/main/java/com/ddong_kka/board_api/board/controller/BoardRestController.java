package com.ddong_kka.board_api.board.controller;

import com.ddong_kka.board_api.board.dto.BoardResponseDto;
import com.ddong_kka.board_api.board.dto.BoardWriteDto;
import com.ddong_kka.board_api.board.service.BoardService;
import com.ddong_kka.board_api.exception.BoardAlreadyDeletedException;
import com.ddong_kka.board_api.exception.BoardNotFoundException;
import com.ddong_kka.board_api.exception.UnauthorizedAccessException;
import com.ddong_kka.board_api.exception.UserNotFoundException;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/v1/board")
@AllArgsConstructor
public class BoardRestController {

    private final BoardService boardService;
    private final Logger logger =  LoggerFactory.getLogger(BoardRestController.class);


    @GetMapping("")
    public ResponseEntity<?> boardList(@RequestParam(value="page", defaultValue = "0") int page) {
        try{
            Page<BoardResponseDto> paging = boardService.getList(page);

            return ResponseEntity.ok(paging);
        } catch(IllegalArgumentException e){
            logger.warn("Invalid page number - {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "status", HttpStatus.UNAUTHORIZED.value(),
                            "error", "Invalid page number",
                            "message", e.getMessage()
                    ));
        } catch (Exception e){
            logger.error("Unexpected Error - {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "status", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "error", "Unexpected Error",
                            "message", "예상치 못한 오류가 발생하였습니다."
                    ));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> detail(@PathVariable("id") Long id){
        try {
            return ResponseEntity.ok(boardService.getDetail(id));
        } catch(IllegalArgumentException e){
            logger.warn("Invalid ID- {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "status", HttpStatus.UNAUTHORIZED.value(),
                            "error", "Invalid ID",
                            "message", e.getMessage()
                    ));
        } catch (BoardNotFoundException e) {
            logger.warn("Board NotFound - {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "status", HttpStatus.NOT_FOUND.value(),
                            "error", "Board Not Found",
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

    @PostMapping(value = "")
    public ResponseEntity<?> write(
            @RequestPart("boardWriteDto") @Valid BoardWriteDto boardWriteDto, // JSON 데이터
            BindingResult bindingResult,
            @RequestPart("imageFile") MultipartFile imageFile, // 이미지 파일
            @RequestHeader("Authorization") String authorizationHeader
    ){

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
                        "message", errors
                );

                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            Long board_id = boardService.saveBoard(boardWriteDto,imageFile,jwtToken);


            Map<String,Object> successResponse = Map.of(
                    "message", "게시글 작성 성공",
                    "board_id", "1"
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
        }  catch (Exception e) {
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
                        "message", errors
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
            logger.warn("JWT Token NotFond - {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "status", HttpStatus.UNAUTHORIZED.value(),
                            "error", "Missing JWT Token",
                            "message", e.getMessage()
                    ));
        } catch (UserNotFoundException e) {
            logger.warn("User NotFound - {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "status", HttpStatus.NOT_FOUND.value(),
                            "error", "User Not Found",
                            "message", e.getMessage()
                    ));
        } catch (BoardNotFoundException e) {
            logger.warn("Board NotFound - {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "status", HttpStatus.NOT_FOUND.value(),
                            "error", "Board Not Found",
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


    @DeleteMapping("{id}")
    public ResponseEntity<?> delete(@RequestHeader("Authorization") String authorizationHeader
                                    , @PathVariable("id") Long id) {

        String jwtToken;
        try{
            if (authorizationHeader != null && !authorizationHeader.startsWith("Bearer ")) {
                throw new IllegalArgumentException("JWT token is missing or invalid");
            }

            jwtToken = authorizationHeader.substring(7);  // 'Bearer ' 부분을 제거하고 JWT만 추출

            boardService.deleteBoard(id,jwtToken);

            Map<String,Object> successResponse = Map.of(
                    "message", "게시글 삭제 성공"
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
        } catch (BoardAlreadyDeletedException e){
            logger.warn("Board Already Delete - {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "status", HttpStatus.NOT_FOUND.value(),
                            "error", "Board Already Delete",
                            "message", e.getMessage()
                    ));
        } catch (UserNotFoundException e) {
            logger.warn("User NotFound - {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "status", HttpStatus.NOT_FOUND.value(),
                            "error", "User Not Found",
                            "message", e.getMessage()
                    ));
        } catch (BoardNotFoundException e) {
            logger.warn("Board NotFound - {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "status", HttpStatus.NOT_FOUND.value(),
                            "error", "Board Not Found",
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
}
