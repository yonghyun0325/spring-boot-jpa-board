# Spring Boot JPA Board

Spring Boot와 Spring Data JPA를 학습하기 위해 구현한 REST API 기반 게시판 프로젝트입니다.

기존에 학습했던 Servlet/JSP/JDBC 게시판과 Spring MVC/JSP/MyBatis 게시판을 바탕으로,  
이번 프로젝트에서는 Spring Boot, REST API, JPA를 사용하여 게시판을 다시 구현했습니다.

---

## 1. 프로젝트 목적

게시판 CRUD를 동일한 요구사항으로 반복 구현하면서  
Java 웹 개발 기술의 변화와 각 기술의 차이를 이해하는 것을 목표로 했습니다.

학습 흐름은 다음과 같습니다.

```text
Servlet + JSP + JDBC
        ↓
Spring MVC + JSP + MyBatis
        ↓
Spring Boot + REST API + JPA
```

이번 프로젝트에서는 다음 내용을 중점적으로 학습했습니다.

- Spring Boot 기반 REST API 설계
- Spring Data JPA Repository
- JPA Dirty Checking
- DTO를 통한 Entity와 API 응답 분리
- Validation
- Global Exception Handling
- 검색 및 페이징
- Service 단위 테스트
- Controller 테스트
- 테스트 환경 분리

---

## 2. 기술 스택

### Backend

- Java 17
- Spring Boot 4.1.0
- Spring MVC
- Spring Data JPA
- Hibernate
- Bean Validation
- Gradle

### Database

- PostgreSQL 17

### Test

- JUnit 5
- Mockito
- AssertJ
- MockMvc
- H2 Database

### Development

- IntelliJ IDEA
- Git
- GitHub

---

## 3. 프로젝트 구조

```text
src
├── main
│   ├── java/com/example/board
│   │   ├── controller
│   │   │   └── BoardController.java
│   │   │
│   │   ├── dto
│   │   │   ├── BoardCreateRequest.java
│   │   │   ├── BoardUpdateRequest.java
│   │   │   ├── BoardResponse.java
│   │   │   └── BoardPageResponse.java
│   │   │
│   │   ├── entity
│   │   │   └── Board.java
│   │   │
│   │   ├── exception
│   │   │   ├── BoardNotFoundException.java
│   │   │   ├── ErrorResponse.java
│   │   │   └── GlobalExceptionHandler.java
│   │   │
│   │   ├── repository
│   │   │   └── BoardRepository.java
│   │   │
│   │   ├── service
│   │   │   └── BoardService.java
│   │   │
│   │   └── SpringBootJpaBoardApplication.java
│   │
│   └── resources
│       └── application.properties
│
└── test
    ├── java/com/example/board
    │   ├── controller
    │   │   └── BoardControllerTest.java
    │   ├── service
    │   │   └── BoardServiceTest.java
    │   └── SpringBootJpaBoardApplicationTests.java
    │
    └── resources
        └── application-test.properties
```

---

## 4. 주요 기능

### 게시글 등록

게시글의 제목, 내용, 작성자를 입력하여 새로운 게시글을 등록할 수 있습니다.

```http
POST /api/boards
```

요청:

```json
{
  "title": "Spring Boot 게시판",
  "content": "게시글 내용입니다.",
  "writer": "이용현"
}
```

정상 등록 시:

```text
201 Created
```

---

### 게시글 목록 조회

```http
GET /api/boards
```

게시글은 페이징하여 조회할 수 있습니다.

```http
GET /api/boards?page=1&size=5
```

---

### 게시글 상세 조회

```http
GET /api/boards/{id}
```

게시글을 상세 조회하면 조회수가 증가합니다.

JPA의 Dirty Checking을 이용하여 별도의 `save()` 호출 없이  
조회수 변경 내용을 데이터베이스에 반영합니다.

---

### 게시글 수정

```http
PUT /api/boards/{id}
```

요청:

```json
{
  "title": "수정된 제목",
  "content": "수정된 내용입니다.",
  "writer": "이용현"
}
```

수정 역시 영속 상태의 Entity를 변경한 뒤  
JPA Dirty Checking을 통해 데이터베이스에 반영합니다.

---

### 게시글 삭제

```http
DELETE /api/boards/{id}
```

정상 삭제 시:

```text
204 No Content
```

를 반환합니다.

---

### 제목 검색

Spring Data JPA Query Method를 이용하여 제목 검색 기능을 구현했습니다.

```http
GET /api/boards?keyword=Spring
```

Repository에서는 다음과 같은 Query Method를 사용합니다.

```java
Page<Board> findByTitleContainingIgnoreCase(
        String keyword,
        Pageable pageable
);
```

MyBatis처럼 검색 SQL을 직접 작성하지 않고  
메서드 이름을 통해 검색 조건을 정의했습니다.

---

### 검색 + 페이징

검색과 페이징을 동시에 사용할 수 있습니다.

```http
GET /api/boards?keyword=Spring&page=1&size=5
```

응답 예시:

```json
{
  "content": [],
  "currentPage": 1,
  "totalPages": 2,
  "totalElements": 7,
  "size": 5,
  "hasNext": true,
  "hasPrevious": false
}
```

Spring Data JPA의 `Page`, `Pageable`, `PageRequest`를 사용하여 구현했습니다.

---

## 5. Validation

잘못된 요청이 Service 및 Database까지 전달되지 않도록  
Bean Validation을 적용했습니다.

예:

```java
@NotBlank(message = "제목은 필수입니다.")
@Size(max = 200, message = "제목은 200자 이하로 입력해주세요.")
private String title;
```

다음과 같은 요청은 거부됩니다.

```json
{
  "title": "",
  "content": "",
  "writer": ""
}
```

응답:

```text
400 Bad Request
```

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "제목은 필수입니다."
}
```

---

## 6. Exception Handling

존재하지 않는 게시글을 조회할 경우  
`BoardNotFoundException`을 발생시키도록 구현했습니다.

```text
BoardService
     ↓
BoardNotFoundException
     ↓
GlobalExceptionHandler
     ↓
404 Not Found
```

예:

```http
GET /api/boards/999999
```

응답:

```json
{
  "status": 404,
  "error": "Not Found",
  "message": "게시글을 찾을 수 없습니다. id=999999"
}
```

Controller마다 `try-catch`를 작성하지 않고  
`@RestControllerAdvice`를 이용해 공통 예외 처리를 구현했습니다.

---

## 7. JPA Dirty Checking

게시글 수정과 조회수 증가에서는 Repository의 `save()`를 다시 호출하지 않습니다.

```java
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
```

트랜잭션 안에서 조회한 Entity는 영속 상태이므로,  
Entity의 상태가 변경되면 트랜잭션 종료 시 Hibernate가 변경을 감지하여  
UPDATE SQL을 실행합니다.

---

## 8. 테스트

### Service Test

Mockito를 사용하여 Repository를 Mock 객체로 대체하고  
Service 로직을 단위 테스트했습니다.

검증 항목:

- 게시글 상세 조회
- 조회수 증가
- 존재하지 않는 게시글 예외
- 게시글 등록
- 게시글 수정
- 게시글 삭제
- Repository 호출 여부

---

### Controller Test

`@WebMvcTest`와 MockMvc를 이용하여 REST API를 테스트했습니다.

검증 항목:

```text
POST   /api/boards       → 201 Created
POST   /api/boards       → Validation 실패 시 400
GET    /api/boards/{id}  → 200 OK
GET    /api/boards/{id}  → 존재하지 않을 경우 404
PUT    /api/boards/{id}  → 200 OK
DELETE /api/boards/{id}  → 204 No Content
GET    /api/boards       → 검색 + 페이징
```

---

## 9. 테스트 환경 분리

개발 환경에서는 PostgreSQL을 사용하고,  
자동 테스트에서는 H2 In-Memory Database를 사용합니다.

```text
Development
Spring Boot
    ↓
PostgreSQL

Test
Spring Boot Test
    ↓
H2

Service Unit Test
    ↓
Mockito
```

테스트 실행:

```bash
./gradlew clean test
```

Windows:

```powershell
.\gradlew clean test
```

전체 테스트 실행 결과:

```text
BUILD SUCCESSFUL
```

---

## 10. 실행 환경 설정

데이터베이스 접속 정보는 코드에 직접 작성하지 않고  
환경변수를 사용합니다.

```properties
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PASSWORD}
```

예:

```text
DB_URL=jdbc:postgresql://localhost:5432/spring_boot_board
DB_USER=postgres
DB_PASSWORD=your-password
```

민감정보는 Git Repository에 저장하지 않습니다.

---

## 11. 이전 프로젝트와 비교

### Servlet + JDBC

```text
Controller 역할 직접 구현
Connection 직접 관리
SQL 직접 작성
ResultSet 직접 변환
```

### Spring MVC + MyBatis

```text
Spring MVC Controller
Service / Repository 계층 분리
Mapper XML
SQL 직접 작성
```

### Spring Boot + JPA

```text
REST Controller
DTO
Service
Spring Data JPA Repository
Entity
Dirty Checking
Query Method
Pageable
Global Exception Handling
Validation
Automatic Test
```

동일한 게시판 기능을 세 가지 방식으로 구현하면서  
Java 웹 애플리케이션이 발전해온 흐름과 각 기술의 역할을 비교했습니다.

---

## 12. API 정리

| Method | URL | 기능 | 정상 응답 |
|---|---|---|---|
| POST | `/api/boards` | 게시글 등록 | 201 |
| GET | `/api/boards` | 게시글 목록 | 200 |
| GET | `/api/boards/{id}` | 게시글 상세 | 200 |
| PUT | `/api/boards/{id}` | 게시글 수정 | 200 |
| DELETE | `/api/boards/{id}` | 게시글 삭제 | 204 |
| GET | `/api/boards?keyword=Spring` | 제목 검색 | 200 |
| GET | `/api/boards?page=1&size=5` | 페이징 | 200 |
| GET | `/api/boards?keyword=Spring&page=1&size=5` | 검색 + 페이징 | 200 |

---

## 13. 학습 내용

이 프로젝트를 통해 다음 내용을 학습했습니다.

- Spring Boot 기반 REST API 개발
- Spring Data JPA 사용법
- Entity 영속성
- Dirty Checking
- Transaction
- DTO 분리
- Spring Data Query Method
- Pageable / Page
- Bean Validation
- Global Exception Handler
- Mockito 기반 단위 테스트
- MockMvc 기반 Controller 테스트
- 개발 DB와 테스트 DB 환경 분리
