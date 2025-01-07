package com.ddong_kka.board_api.comment.domain;


import com.ddong_kka.board_api.board.domain.Board;
import com.ddong_kka.board_api.user.domain.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Getter
@Setter
@ToString
@NoArgsConstructor( access = AccessLevel.PROTECTED)
@Entity
@Table(name = "comment")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    @Column(length = 100 , nullable = false)
    private String content;

    @CreationTimestamp
    private Timestamp createAt;

    @UpdateTimestamp
    private Timestamp modifyAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "boardId", nullable = false)
    private Board board;

    @Builder
    public Comment( String content, User user, Board board) {
        this.content = content;
        this.user = user;
        this.board = board;
    }

}
