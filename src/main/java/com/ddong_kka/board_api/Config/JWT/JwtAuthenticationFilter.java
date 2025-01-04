package com.ddong_kka.board_api.Config.JWT;

import com.ddong_kka.board_api.Config.auth.PrincipalDetails;
import com.ddong_kka.board_api.user.domain.User;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    //생성자 : jwtUtil 객체를 주입받아 초기화
    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 헤더에서 access 키에 담긴 토큰을 가져온다.

        String headerAuthorizationToken  = request.getHeader("Authorization");

        // 토큰이 없다면 다음 필터로 넘긴다.
        if (headerAuthorizationToken  == null || !headerAuthorizationToken.startsWith("Bearer ")){
            filterChain.doFilter(request,response);
            return;
        }

        // 토큰 만료 여부 확인, 만료시 다음 필터로 넘기지 않음
        // 만료시 오류가 발생하기에 catch 문의 코드 실행
        // Bearer 분리는 서버에서 처리
        String accessToken = headerAuthorizationToken .split(" ")[1];

        try{
            jwtUtil.isExpired(accessToken);
        }catch (ExpiredJwtException e){
            PrintWriter writer = response.getWriter();
            writer.print("access token expired");

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // 토큰이 access 토큰인지 확인 (발급시 페이로드에 명시)
        String category = jwtUtil.getCategory(accessToken);

        if (!category.equals("access")){

            PrintWriter writer = response.getWriter();
            writer.print("invalid access token");

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

            return;
        }


        // 토큰 검증이 완료되면 email 과 role 값을 가져온다.
        String email = jwtUtil.getEmail(accessToken);
        String role = jwtUtil.getRole(accessToken);

        User usersEntity =  User.builder()
                .email(email)
                .role(role)
                .build();

        // PrincipalDetails 객체 생성 (인증 사용자 정보)
        PrincipalDetails principalDetails = new PrincipalDetails(usersEntity);

        // 스프링 시큐리티 인증 토큰 생성
        Authentication authToken = new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request,response);
    }
}
