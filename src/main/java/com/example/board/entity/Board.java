package com.example.board.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "board")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false, length = 50)
    private String writer;

    @Column(nullable = false)
    private int viewCount;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Board(
            String title,
            String content,
            String writer
    ) {
        this.title = title;
        this.content = content;
        this.writer = writer;
        this.viewCount = 0;
        this.createdAt = LocalDateTime.now();
    }

    public void update(
            String title,
            String content,
            String writer
    ) {
        this.title = title;
        this.content = content;
        this.writer = writer;
    }

    public void increaseViewCount() {
        this.viewCount++;
    }
}