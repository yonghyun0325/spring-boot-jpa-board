package com.example.board.service;

import com.example.board.dto.BoardCreateRequest;
import com.example.board.dto.BoardPageResponse;
import com.example.board.dto.BoardResponse;
import com.example.board.dto.BoardUpdateRequest;
import com.example.board.entity.Board;
import com.example.board.exception.BoardNotFoundException;
import com.example.board.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;

    @Transactional(readOnly = true)
    public BoardPageResponse findAll(
            String keyword,
            int page,
            int size
    ) {

        Pageable pageable =
                PageRequest.of(
                        page - 1,
                        size,
                        Sort.by(
                                Sort.Direction.DESC,
                                "id"
                        )
                );

        Page<Board> boards;

        if (keyword == null || keyword.isBlank()) {

            boards =
                    boardRepository.findAll(pageable);

        } else {

            boards =
                    boardRepository
                            .findByTitleContainingIgnoreCase(
                                    keyword,
                                    pageable
                            );
        }

        Page<BoardResponse> responses =
                boards.map(BoardResponse::new);

        return new BoardPageResponse(responses);
    }

    @Transactional
    public BoardResponse create(BoardCreateRequest request) {

        Board board = new Board(
                request.getTitle(),
                request.getContent(),
                request.getWriter()
        );

        Board savedBoard =
                boardRepository.save(board);

        return new BoardResponse(savedBoard);
    }

    @Transactional
    public BoardResponse findById(Long id) {

        Board board = boardRepository.findById(id)
                .orElseThrow(
                        () -> new BoardNotFoundException(id)
                );

        board.increaseViewCount();

        return new BoardResponse(board);
    }

    @Transactional
    public BoardResponse update(
            Long id,
            BoardUpdateRequest request
    ) {

        Board board = boardRepository.findById(id)
                .orElseThrow(
                        () -> new BoardNotFoundException(id)
                );

        board.update(
                request.getTitle(),
                request.getContent(),
                request.getWriter()
        );

        return new BoardResponse(board);
    }

    @Transactional
    public void delete(Long id) {

        Board board = boardRepository.findById(id)
                .orElseThrow(
                        () -> new BoardNotFoundException(id)
                );

        boardRepository.delete(board);
    }
}