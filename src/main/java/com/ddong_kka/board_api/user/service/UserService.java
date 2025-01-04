package com.ddong_kka.board_api.user.service;

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
        User user = User.builder()
                .email(userRegisterDto.getEmail())
                .password(passwordEncoder.encode(userRegisterDto.getPassword()))
                .role("ROLE_USER")
                .build();

        userRepository.save(user);
    }
}
