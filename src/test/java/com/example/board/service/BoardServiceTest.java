package com.example.board.service;

import com.example.board.dto.BoardCreateRequest;
import com.example.board.dto.BoardResponse;
import com.example.board.dto.BoardUpdateRequest;
import com.example.board.entity.Board;
import com.example.board.exception.BoardNotFoundException;
import com.example.board.repository.BoardRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BoardServiceTest {

    @Mock
    private BoardRepository boardRepository;

    @InjectMocks
    private BoardService boardService;

    @Test
    void 게시글을_조회하면_조회수가_증가한다() {

        // given
        Board board = new Board(
                "Spring Boot 테스트",
                "Service 테스트입니다.",
                "이용현"
        );

        given(boardRepository.findById(1L))
                .willReturn(Optional.of(board));

        // when
        BoardResponse response =
                boardService.findById(1L);

        // then
        assertThat(response.getTitle())
                .isEqualTo("Spring Boot 테스트");

        assertThat(response.getViewCount())
                .isEqualTo(1);
    }

    @Test
    void 존재하지_않는_게시글을_조회하면_예외가_발생한다() {

        // given
        given(boardRepository.findById(999L))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(
                () -> boardService.findById(999L)
        )
                .isInstanceOf(BoardNotFoundException.class)
                .hasMessage("게시글을 찾을 수 없습니다. id=999");
    }

    @Test
    void 게시글을_등록한다() {

        // given
        BoardCreateRequest request =
                mock(BoardCreateRequest.class);

        given(request.getTitle())
                .willReturn("Spring Boot 등록 테스트");

        given(request.getContent())
                .willReturn("게시글 등록 Service 테스트입니다.");

        given(request.getWriter())
                .willReturn("이용현");

        given(boardRepository.save(any(Board.class)))
                .willAnswer(invocation ->
                        invocation.getArgument(0)
                );

        // when
        BoardResponse response =
                boardService.create(request);

        // then
        assertThat(response.getTitle())
                .isEqualTo("Spring Boot 등록 테스트");

        assertThat(response.getContent())
                .isEqualTo("게시글 등록 Service 테스트입니다.");

        assertThat(response.getWriter())
                .isEqualTo("이용현");

        assertThat(response.getViewCount())
                .isZero();

        verify(boardRepository)
                .save(any(Board.class));
    }

    @Test
    void 게시글을_수정한다() {

        // given
        Board board = new Board(
                "기존 제목",
                "기존 내용",
                "이용현"
        );

        BoardUpdateRequest request =
                mock(BoardUpdateRequest.class);

        given(boardRepository.findById(1L))
                .willReturn(Optional.of(board));

        given(request.getTitle())
                .willReturn("수정된 제목");

        given(request.getContent())
                .willReturn("수정된 내용");

        given(request.getWriter())
                .willReturn("이용현");

        // when
        BoardResponse response =
                boardService.update(1L, request);

        // then
        assertThat(response.getTitle())
                .isEqualTo("수정된 제목");

        assertThat(response.getContent())
                .isEqualTo("수정된 내용");

        assertThat(response.getWriter())
                .isEqualTo("이용현");
    }

    @Test
    void 게시글을_삭제한다() {

        // given
        Board board = new Board(
                "삭제할 게시글",
                "삭제 테스트입니다.",
                "이용현"
        );

        given(boardRepository.findById(1L))
                .willReturn(Optional.of(board));

        // when
        boardService.delete(1L);

        // then
        verify(boardRepository)
                .delete(board);
    }

    @Test
    void 존재하지_않는_게시글을_삭제하면_예외가_발생한다() {

        // given
        given(boardRepository.findById(999L))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(
                () -> boardService.delete(999L)
        )
                .isInstanceOf(BoardNotFoundException.class)
                .hasMessage("게시글을 찾을 수 없습니다. id=999");
    }
}