package com.example.board.controller;

import com.example.board.dto.BoardCreateRequest;
import com.example.board.dto.BoardPageResponse;
import com.example.board.dto.BoardResponse;
import com.example.board.dto.BoardUpdateRequest;
import com.example.board.entity.Board;
import com.example.board.exception.BoardNotFoundException;
import com.example.board.service.BoardService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BoardController.class)
class BoardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BoardService boardService;

    @Test
    void 제목이_비어있으면_게시글_등록에_실패한다() throws Exception {

        // given
        String request = """
                {
                  "title": "",
                  "content": "게시글 내용입니다.",
                  "writer": "이용현"
                }
                """;

        // when & then
        mockMvc.perform(
                        post("/api/boards")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message")
                        .value("제목은 필수입니다."));
    }

    @Test
    void 존재하지_않는_게시글을_조회하면_404를_반환한다() throws Exception {

        // given
        given(boardService.findById(999L))
                .willThrow(new BoardNotFoundException(999L));

        // when & then
        mockMvc.perform(
                        get("/api/boards/999")
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message")
                        .value("게시글을 찾을 수 없습니다. id=999"));
    }

    @Test
    void 게시글을_등록하면_201을_반환한다() throws Exception {

        // given
        Board board = new Board(
                "Spring Boot 테스트",
                "Controller 등록 테스트입니다.",
                "이용현"
        );

        BoardResponse response =
                new BoardResponse(board);

        given(boardService.create(any(BoardCreateRequest.class)))
                .willReturn(response);

        String request = """
            {
              "title": "Spring Boot 테스트",
              "content": "Controller 등록 테스트입니다.",
              "writer": "이용현"
            }
            """;

        // when & then
        mockMvc.perform(
                        post("/api/boards")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title")
                        .value("Spring Boot 테스트"))
                .andExpect(jsonPath("$.content")
                        .value("Controller 등록 테스트입니다."))
                .andExpect(jsonPath("$.writer")
                        .value("이용현"))
                .andExpect(jsonPath("$.viewCount")
                        .value(0));
    }

    @Test
    void 게시글을_조회하면_200을_반환한다() throws Exception {

        // given
        Board board = new Board(
                "Spring Boot 상세 조회",
                "Controller 조회 테스트입니다.",
                "이용현"
        );

        BoardResponse response =
                new BoardResponse(board);

        given(boardService.findById(1L))
                .willReturn(response);

        // when & then
        mockMvc.perform(
                        get("/api/boards/1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title")
                        .value("Spring Boot 상세 조회"))
                .andExpect(jsonPath("$.content")
                        .value("Controller 조회 테스트입니다."))
                .andExpect(jsonPath("$.writer")
                        .value("이용현"))
                .andExpect(jsonPath("$.viewCount")
                        .value(0));
    }

    @Test
    void 게시글을_수정하면_200을_반환한다() throws Exception {

        // given
        Board board = new Board(
                "수정된 제목",
                "수정된 내용입니다.",
                "이용현"
        );

        BoardResponse response =
                new BoardResponse(board);

        given(
                boardService.update(
                        eq(1L),
                        any(BoardUpdateRequest.class)
                )
        )
                .willReturn(response);

        String request = """
            {
              "title": "수정된 제목",
              "content": "수정된 내용입니다.",
              "writer": "이용현"
            }
            """;

        // when & then
        mockMvc.perform(
                        put("/api/boards/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title")
                        .value("수정된 제목"))
                .andExpect(jsonPath("$.content")
                        .value("수정된 내용입니다."))
                .andExpect(jsonPath("$.writer")
                        .value("이용현"));
    }

    @Test
    void 게시글을_삭제하면_204를_반환한다() throws Exception {

        // when & then
        mockMvc.perform(
                        delete("/api/boards/1")
                )
                .andExpect(status().isNoContent());

        verify(boardService)
                .delete(1L);
    }

    @Test
    void 게시글을_검색하고_페이징해서_조회한다() throws Exception {

        // given
        Board board1 = new Board(
                "Spring Boot 게시판",
                "첫 번째 게시글입니다.",
                "이용현"
        );

        Board board2 = new Board(
                "Spring Data JPA",
                "두 번째 게시글입니다.",
                "이용현"
        );

        BoardPageResponse response =
                new BoardPageResponse(
                        new PageImpl<>(
                                List.of(
                                        new BoardResponse(board1),
                                        new BoardResponse(board2)
                                ),
                                PageRequest.of(0, 2),
                                3
                        )
                );

        given(
                boardService.findAll(
                        "Spring",
                        1,
                        2
                )
        ).willReturn(response);

        // when & then
        mockMvc.perform(
                        get("/api/boards")
                                .param("keyword", "Spring")
                                .param("page", "1")
                                .param("size", "2")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()")
                        .value(2))
                .andExpect(jsonPath("$.content[0].title")
                        .value("Spring Boot 게시판"))
                .andExpect(jsonPath("$.content[1].title")
                        .value("Spring Data JPA"))
                .andExpect(jsonPath("$.currentPage")
                        .value(1))
                .andExpect(jsonPath("$.totalPages")
                        .value(2))
                .andExpect(jsonPath("$.totalElements")
                        .value(3))
                .andExpect(jsonPath("$.size")
                        .value(2))
                .andExpect(jsonPath("$.hasNext")
                        .value(true))
                .andExpect(jsonPath("$.hasPrevious")
                        .value(false));
    }
}