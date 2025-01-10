# 프로젝트 이름

JWT 기반 게시글 관리 API

작성중임

## 개요

이 프로젝트는 JWT(JSON Web Token)를 활용하여 인증 및 권한 관리를 구현하고, 게시글 작성, 조회, 수정, 삭제와 같은 기본적인 CRUD 기능을 제공하는 간단한 RESTful API입니다.

## 주요 기능

- JWT 기반 사용자 인증 및 권한 관리
- 게시글 작성, 조회, 수정, 삭제 기능 제공
- 댓글 작성, 조회, 수정, 삭제 기능 제공
- RESTful API 아키텍처 준수
- 예외 처리 및 유효성 검사 구현

## 기술 스택

- **백엔드 프레임워크**: Spring Boot 3.x
- **보안**: Spring Security 6.x
- **데이터베이스**: PostgreSQL
- **서버**: Apache Tomcat 9.0

## API 명세서

### 인증

#### JWT 발급
- **URL**: `/auth/login`
- **메서드**: POST
- **요청**:
  ```json
  {
    "email": "사용자 이메일",
    "password": "사용자 비밀번호"
  }
  ```
- **응답**:
  ```json
  {
    "accessToken": "JWT 토큰",
    "refreshToken": "리프레시 토큰"
  }
  ```


#### JWT 검증
- **URL**: `/auth/validate`
- **메서드**: POST
- **요청**:
  ```json
  {
    "token": "JWT 토큰"
  }
  ```
- **응답**:
  ```json
  {
    "valid": true
  }
  ```

### 게시글

#### 게시글 작성
- **URL**: `/posts`
- **메서드**: POST
- **요청**:
  ```json
  {
    "title": "게시글 제목",
    "content": "게시글 내용"
  }
  ```
- **응답**:
  ```json
  {
    "id": 1,
    "title": "게시글 제목",
    "content": "게시글 내용",
    "createdAt": "2025-01-01T12:00:00Z"
  }
  ```

#### 게시글 조회
- **URL**: `/posts/{id}`
- **메서드**: GET

#### 게시글 수정
- **URL**: `/posts/{id}`
- **메서드**: PUT
- **요청**:
  ```json
  {
    "title": "수정된 제목",
    "content": "수정된 내용"
  }
  ```

#### 게시글 삭제
- **URL**: `/posts/{id}`
- **메서드**: DELETE

## 설치 및 실행 방법

1. 이 저장소를 클론합니다.
   ```bash
   git clone https://github.com/username/repository.git
   ```

2. 프로젝트 디렉터리로 이동합니다.
   ```bash
   cd repository
   ```

3. 필요한 의존성을 설치하고 애플리케이션을 실행합니다.
   ```bash
   ./mvnw spring-boot:run
   ```

4. API 서버는 기본적으로 `http://localhost:8080`에서 실행됩니다.

## 테스트

- Postman 또는 cURL을 사용하여 API 요청을 테스트할 수 있습니다.
- 예제를 실행하기 위해 다음과 같은 명령어를 사용할 수 있습니다.

```bash
curl -X POST http://localhost:8080/auth/login -H "Content-Type: application/json" -d '{"username":"testuser","password":"testpassword"}'
```

## 라이선스

이 프로젝트는 [MIT 라이선스](LICENSE)를 따릅니다.

## 문의

- 이메일: example@example.com
- 깃허브: [username](https://github.com/username)

