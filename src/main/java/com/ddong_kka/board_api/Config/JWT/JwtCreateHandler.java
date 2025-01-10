package com.ddong_kka.board_api.Config.JWT;

import com.ddong_kka.board_api.Config.JWT.domain.RefreshToken;
import com.ddong_kka.board_api.Config.JWT.repository.RefreshTokenRepository;
import com.ddong_kka.board_api.Config.auth.PrincipalDetails;
import jakarta.servlet.ServletException;

import jakarta.servlet.http.Cookie;
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
import java.util.Date;
import java.util.Iterator;

@Component
public class JwtCreateHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    public JwtCreateHandler(JwtUtil jwtUtil, RefreshTokenRepository refreshTokenRepository) {
        this.jwtUtil = jwtUtil;
        this.refreshTokenRepository = refreshTokenRepository;
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
        String refresh =  jwtUtil.createJwt("refresh",email,role,86400000L); // 생명주기 24시간

        // Refresh 토큰 저장
        addRefreshEntity(email,refresh,86400000L);


        // 토큰 발급 성공 시 메인 화면으로 리다이렉트
        // 엑세스 토큰은 헤더에 저장하고 리프레쉬 토큰은 쿠키에 저장한다.
        //응답 설정

        // Print the authentication details for debugging
        response.addCookie(createCookie("refresh", refresh));

        // Create and set the authentication in the SecurityContext
        Authentication authToken = new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);
        // JSON 응답 생성
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // JSON 객체 생성
        String jsonResponse = String.format("{\"message\": \"jwt 발급 성공\"");


        response.setHeader("Authorization","Bearer " +  access);
        response.getWriter().write(jsonResponse);
        response.setStatus(HttpStatus.OK.value());
    }

    // 쿠키 생성 메소드
    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value); // 쿠키 객체 생성
        cookie.setMaxAge(24 * 60 * 60); // 쿠키의 유효 기간 설정 (60시간)
        // cookie.setSecure(true); // https 환경에서만 쿠키 사용
        cookie.setPath("/"); // 모든 경로에서 쿠키 사용 가능
        cookie.setHttpOnly(true); // 자바스크립트에서 쿠키 접근 불가
        return cookie; // 생성한 쿠키 반환
    }

    // 서버에 refresh 토큰을 저장하는 메소드
    private void addRefreshEntity(String email, String refresh, Long expiredMs) {

        Date date = new Date(System.currentTimeMillis() + expiredMs); // 만료일자 생성

        RefreshToken refreshToken = RefreshToken.builder()
                .email(email)
                .refresh(refresh)
                .expiration(date.toString())
                .build();

        refreshTokenRepository.save(refreshToken);
    }
}
