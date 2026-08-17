package com.example.board.dto;

import com.example.board.entity.Board;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class BoardResponse {

    private final Long id;
    private final String title;
    private final String content;
    private final String writer;
    private final int viewCount;
    private final LocalDateTime createdAt;

    public BoardResponse(Board board) {
        this.id = board.getId();
        this.title = board.getTitle();
        this.content = board.getContent();
        this.writer = board.getWriter();
        this.viewCount = board.getViewCount();
        this.createdAt = board.getCreatedAt();
    }
}