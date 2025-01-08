package com.ddong_kka.board_api.deleteBoard.domain;


import com.ddong_kka.board_api.board.domain.Board;
import com.ddong_kka.board_api.user.domain.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Getter
@Setter
@ToString
@NoArgsConstructor( access = AccessLevel.PROTECTED)
@Entity
@Table(name = "deleteBoard")
public class DeleteBoard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long deleteBoardId;

    @Column(length = 30, nullable = false)
    private String title;

    @Column(length = 500 , nullable = false)
    private String content;

    @Column(nullable = false)
    private int views;

    private Timestamp createAt;

    @CreationTimestamp
    private Timestamp deleteAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @Builder
    public DeleteBoard(Board board) {
        this.title = board.getTitle();
        this.content = board.getContent();
        this.views = board.getViews();
        this.createAt = board.getCreateAt();
        this.user = board.getUser();
    }
}