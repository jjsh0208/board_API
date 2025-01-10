package com.ddong_kka.board_api.Config.JWT;


import com.ddong_kka.board_api.Config.JWT.repository.RefreshTokenRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@AllArgsConstructor
public class JwtLogoutFilter extends GenericFilterBean {

    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        // 요청 경로와 HTTP 메서드 확인
        String requestUri = request.getRequestURI();
        if (!requestUri.matches("^\\/logout$")) { //로그아웃 경로인지 확인
            filterChain.doFilter(request, response); // 로그아웃 경로가 아니면 다음 필터로 이동
            return;
        }
        String requestMethod = request.getMethod();
        if (!requestMethod.equals("POST")) { // 로그아웃 경로이지만 post 로 요청된 게 아니면 다음 필터로 이동
            filterChain.doFilter(request, response);
            return;
        }

        // Refresh 토큰 가져오기
        String refresh = null;
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {

            if (cookie.getName().equals("refresh")) { // 쿠키에 refresh 토큰이 존재하는 확인

                refresh = cookie.getValue();
            }
        }

        // refresh 토큰이 없으면 에러 처리
        if (refresh == null) { // refresh 토큰이 없는 경우

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400 응답
            return;
        }

        // refresh 토큰의 만료 여부 확인
        try {
            jwtUtil.isExpired(refresh); // Refresh 토큰 만료 여부 검사
        } catch (ExpiredJwtException e) { // 만료된 경우
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400 응답
            return;
        }

        // Refresh 토큰 확인 (페이로드의 "category" 값이 "refresh"인지 확인)
        String category = jwtUtil.getCategory(refresh); // 해당 토큰이 refresh 토큰인지 확인
        if (!category.equals("refresh")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400 응답
            return;
        }

        // DB에서 Refresh 토큰 존재 여부 확인
        Boolean isExist = refreshTokenRepository.existsByRefresh(refresh);
        if (!isExist) { // DB에 Refresh 토큰이 존재하지 않는 경우
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400 응답
            return;
        }

        // 로그아웃 처리
        // DB에서 Refresh 토큰 삭제
        refreshTokenRepository.deleteByRefresh(refresh);

        // 쿠키에서 Refresh 토큰 제거
        Cookie cookie = new Cookie("refresh", null);
        cookie.setMaxAge(0); // 쿠키 만료 설정
        cookie.setPath("/");

        // JSON 응답 생성
        response.setContentType("application/json"); // 응답 형식
        response.setCharacterEncoding("UTF-8");

        // JSON 메시지 생성
        String jsonResponse = String.format("{\"message\": \"refresh Token 제거 성공\"");

        response.addCookie(cookie);
        response.getWriter().write(jsonResponse);
        response.setStatus(HttpServletResponse.SC_OK); // 200 응답
    }
}
