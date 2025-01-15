package com.ddong_kka.board_api.user.service;

import com.ddong_kka.board_api.exception.DuplicateEmailException;
import com.ddong_kka.board_api.user.domain.User;
import com.ddong_kka.board_api.user.dto.UserRegisterDto;
import com.ddong_kka.board_api.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void saveUser(UserRegisterDto userRegisterDto) {

        if (userRepository.existsByEmail(userRegisterDto.getEmail())){
            //중복된 이메일이 존재하면 403 응답
            throw new DuplicateEmailException("중복된 이메일입니다. : " + userRegisterDto.getEmail());
        }

        User user = User.builder()
                .email(userRegisterDto.getEmail())
                .password(passwordEncoder.encode(userRegisterDto.getPassword()))
                .role("ROLE_USER")
                .build();

        userRepository.save(user);
    }
}
