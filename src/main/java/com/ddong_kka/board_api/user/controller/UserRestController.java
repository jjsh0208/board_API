package com.ddong_kka.board_api.user.controller;

import com.ddong_kka.board_api.exception.DuplicateEmailException;
import com.ddong_kka.board_api.exception.UnauthorizedAccessException;
import com.ddong_kka.board_api.user.service.UserService;
import com.ddong_kka.board_api.user.dto.UserRegisterDto;
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


@AllArgsConstructor
@RestController
@RequestMapping("api/v1/user")
public class UserRestController {

    private final UserService userService;

    private final Logger logger =  LoggerFactory.getLogger(UserRestController.class);

    @PostMapping(value = {"/", ""})
    public ResponseEntity<?> register(@Valid @RequestBody UserRegisterDto userRegisterDto, BindingResult bindingResult){
        try{
            if (bindingResult.hasErrors()){
                List<Map<String, String>> errors = bindingResult.getFieldErrors().stream()
                        .map(fieldError -> {
                            logger.error("{}: {}", fieldError.getField() , fieldError.getDefaultMessage());
                            return Map.of(
                                    "field" ,fieldError.getField(),
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

            userService.saveUser(userRegisterDto);

            Map<String,Object> successResponse = Map.of(
                    "status" , "success",
                    "message", "회원가입 성공"
            );

            // 상태 코드를 HttpStatus.CREATED로 변경
            return ResponseEntity.status(HttpStatus.CREATED).body(successResponse);
        } catch (DuplicateEmailException e) {
            logger.warn("duplicate e-mail - {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of(
                            "status", HttpStatus.FORBIDDEN.value(),
                            "error", "duplicate e-mail",
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


    @GetMapping("/test")
    @ResponseBody
    public String test(){
        return "test 성공";
    }

}
