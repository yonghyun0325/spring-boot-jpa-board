package com.example.board.controller;

import com.example.board.dto.BoardCreateRequest;
import com.example.board.dto.BoardPageResponse;
import com.example.board.dto.BoardResponse;
import com.example.board.dto.BoardUpdateRequest;
import com.example.board.service.BoardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/boards")
public class BoardController {

    private final BoardService boardService;

    @GetMapping
    public BoardPageResponse getBoards(
            @RequestParam(
                    name = "keyword",
                    required = false
            ) String keyword,

            @RequestParam(
                    name = "page",
                    defaultValue = "1"
            ) int page,

            @RequestParam(
                    name = "size",
                    defaultValue = "5"
            ) int size
    ) {

        return boardService.findAll(
                keyword,
                page,
                size
        );
    }

    @GetMapping("/{id}")
    public BoardResponse getBoard(
            @PathVariable Long id
    ) {
        return boardService.findById(id);
    }

    @PostMapping
    public ResponseEntity<BoardResponse> createBoard(
           @Valid @RequestBody BoardCreateRequest request
    ) {

        BoardResponse response =
                boardService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PutMapping("/{id}")
    public BoardResponse updateBoard(
            @PathVariable Long id,
            @Valid @RequestBody BoardUpdateRequest request
    ) {

        return boardService.update(
                id,
                request
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBoard(
            @PathVariable Long id
    ) {

        boardService.delete(id);

        return ResponseEntity.noContent().build();
    }
}