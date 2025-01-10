package com.ddong_kka.board_api.Config.JWT.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@NoArgsConstructor( access = AccessLevel.PROTECTED)
@Getter
@Setter
@Entity
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userEmail;   // 유저 이메일
    private String refresh;     // 유저가 가지고있는 refresh 토큰
    private String expiration;  // refresh 토큰이 만료되는 시간


    @Builder
    public RefreshToken(String email, String refresh, String expiration) {
        this.userEmail = email;
        this.refresh = refresh;
        this.expiration = expiration;

    }
}
