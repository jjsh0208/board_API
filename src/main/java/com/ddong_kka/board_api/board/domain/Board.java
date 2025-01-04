package com.ddong_kka.board_api.board.domain;

import com.ddong_kka.board_api.user.domain.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Getter
@ToString
@NoArgsConstructor( access = AccessLevel.PROTECTED)
@Entity
@Table(name = "board")
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long board_id;

    @Column(length = 30, nullable = false)
    private String title;

    @Column(length = 500 , nullable = false)
    private String content;

    @Column(nullable = false)
    private int views = 0;

    @Column(nullable = false)
    private int reports = 0;

    @CreationTimestamp
    private Timestamp create_at;

    @UpdateTimestamp
    private Timestamp modify_at;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


    @Builder
    public Board(String title, String content, User user) {
        this.title = title;
        this.content = content;
        this.user = user;
    }
}
