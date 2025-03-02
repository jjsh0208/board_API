package com.ddong_kka.board_api.Config.Security;


import com.ddong_kka.board_api.Config.JWT.JwtAuthenticationFilter;
import com.ddong_kka.board_api.Config.JWT.JwtCreateHandler;
import com.ddong_kka.board_api.Config.JWT.JwtLogoutFilter;
import com.ddong_kka.board_api.Config.JWT.JwtUtil;
import com.ddong_kka.board_api.Config.JWT.domain.RefreshToken;
import com.ddong_kka.board_api.Config.JWT.repository.RefreshTokenRepository;
import com.ddong_kka.board_api.Config.auth.JsonLoginFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtCreateHandler jwtCreateHandler;
    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    public SecurityConfig(JwtCreateHandler jwtCreateHandler, JwtUtil jwtUtil, RefreshTokenRepository refreshTokenRepository) {
        this.jwtCreateHandler = jwtCreateHandler;
        this.jwtUtil = jwtUtil;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Bean
    public BCryptPasswordEncoder encoderPwd() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
        // Rest API 사용으로 httpBasic , csrf 보안을 사용하지 않음
                .csrf(csrf -> csrf.disable())
                .httpBasic(httpBasic -> httpBasic.disable())
                .formLogin(formLogin -> formLogin.disable())
                .addFilterBefore(new JwtAuthenticationFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new JsonLoginFilter(authenticationManager(http.getSharedObject(AuthenticationConfiguration.class))
                        , jwtCreateHandler), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new JwtLogoutFilter(jwtUtil,refreshTokenRepository), LogoutFilter.class)
                .cors(corsCustomizer -> corsCustomizer.configurationSource(corsConfigurationSource())) // cors 설정 메서드 적용
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 시큐리티 세션 사용안함
                .authorizeHttpRequests(authorizationHttpRequest ->
                        authorizationHttpRequest
                                .requestMatchers("/api/v1/user/", "/api/v1/user").permitAll()
                                .requestMatchers("/api/v1/user/**").authenticated()
                                .requestMatchers("/api/v1/board/**").authenticated()
                                .requestMatchers("/api/comment/**").authenticated()
                                .anyRequest().permitAll()
                );

        return http.build();
                        
    }

    // CORS 설정을 한 곳에서 처리하는 메서드
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Collections.singletonList("http://localhost:8080"));
        configuration.setAllowedMethods(Collections.singletonList("*"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(Collections.singletonList("*"));
        configuration.setMaxAge(3600L);
        configuration.setExposedHeaders(Arrays.asList("Set-Cookie", "Authorization"));

        return request -> configuration;
    }
}
