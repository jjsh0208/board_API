package com.ddong_kka.board_api.Config.JWT;

import com.ddong_kka.board_api.Config.auth.PrincipalDetails;
import jakarta.servlet.ServletException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

@Component
public class JwtCreateHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;

    public JwtCreateHandler(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        // 인증된 사용자의 정보를 PrincipalDetails에서 가져옴
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        String email = principalDetails.getUsername(); // 사용자의 이메일

        // 사용자의 권한 정보를 가져옴
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next(); // 첫 번째 권한을 가져옴
        String role = auth.getAuthority(); // 사용자의 역할

        // JWT 토큰 생성 (유효 기간: access : 10분 , refresh : 24시간)
        String access  =  jwtUtil.createJwt("access",email,role, 600000L); // 생명주기 10분
//        String refresh =  jwtUtil.createJwt("refresh",email,role,86400000L); // 생명주기 24시간

        // Create and set the authentication in the SecurityContext
        Authentication authToken = new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);

        response.setHeader("Authorization","Bearer " +  access);
        response.setStatus(HttpStatus.OK.value());
    }
}
