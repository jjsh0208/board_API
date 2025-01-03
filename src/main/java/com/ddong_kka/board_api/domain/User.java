package com.ddong_kka.board_api.domain;


import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
@Table(name = "AppUser")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long user_id;

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
