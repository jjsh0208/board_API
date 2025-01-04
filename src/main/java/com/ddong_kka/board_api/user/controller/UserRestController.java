package com.ddong_kka.board_api.user.controller;

import com.ddong_kka.board_api.user.service.UserService;
import com.ddong_kka.board_api.user.dto.UserRegisterDto;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
                "message", "회원가입 성공"
        );

        return ResponseEntity.ok().body(successResponse);
    }
}
