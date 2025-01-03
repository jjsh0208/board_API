package com.ddong_kka.board_api.Config.auth;

import com.ddong_kka.board_api.domain.User;
import com.ddong_kka.board_api.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PrincipalDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User userEntity = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        if (userEntity != null){ //user가 디비에 존재할때만
            return new PrincipalDetails(userEntity);
        }
        return null;
    }
}
