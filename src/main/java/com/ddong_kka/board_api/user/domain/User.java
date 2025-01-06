package com.ddong_kka.board_api.user.domain;


import jakarta.persistence.*;
import lombok.*;

@Getter
@ToString
@NoArgsConstructor( access = AccessLevel.PROTECTED)
@Entity
@Table(name = "AppUser")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String email;

    private String password;

    private String role;

    @Builder
    public User(String email, String username, String password, String role, String provider, String providerId) {

        this.email = email;
        this.password = password;
        this.role = role;

    }
}
