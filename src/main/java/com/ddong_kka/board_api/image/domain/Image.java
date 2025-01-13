package com.ddong_kka.board_api.image.domain;


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
@Table(name = "image")
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long imageId;

    @Column(length = 100, nullable = false)
    private String originName;

    @Column(length = 100, nullable = false)
    private String saveName;

    @Column(length = 100, nullable = false)
    private String imagePath;

    @Column(length = 100, nullable = false)
    private String imageSize;

    @CreationTimestamp
    private Timestamp createAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "boardId", nullable = false)
    private Board board;

    @Builder
    public Image(String originName, String saveName, String imagePath, String imageSize, Board board) {
        this.originName = originName;
        this.saveName = saveName;
        this.imagePath = imagePath;
        this.imageSize = imageSize;
        this.board = board;
    }

}
