package com.example.board.dto;

import org.springframework.data.domain.Page;

import java.util.List;

public class BoardPageResponse {

    private final List<BoardResponse> content;
    private final int currentPage;
    private final int totalPages;
    private final long totalElements;
    private final int size;
    private final boolean hasNext;
    private final boolean hasPrevious;

    public BoardPageResponse(Page<BoardResponse> page) {
        this.content = page.getContent();
        this.currentPage = page.getNumber() + 1;
        this.totalPages = page.getTotalPages();
        this.totalElements = page.getTotalElements();
        this.size = page.getSize();
        this.hasNext = page.hasNext();
        this.hasPrevious = page.hasPrevious();
    }

    public List<BoardResponse> getContent() {
        return content;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public int getSize() {
        return size;
    }

    public boolean isHasNext() {
        return hasNext;
    }

    public boolean isHasPrevious() {
        return hasPrevious;
    }
}