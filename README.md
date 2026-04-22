# 보험사 금융 IT 인터페이스 통합관리시스템

보험사/금융권 환경에서 운영되는 대내·대외 인터페이스를 통합 관제하기 위한 포트폴리오 프로젝트입니다.
단순 CRUD를 넘어서, 운영자 관점의 상태 관리, 재처리, 장애 로그 분석, 대시보드 기반 모니터링 흐름을 포함합니다.

## 1. 프로젝트 목표
- 인터페이스 자산을 중앙에서 관리(등록/검색/상세/수정/비활성화)
- 운영 상태(RUNNING/WARNING/FAILED/PAUSED) 기반 통합 모니터링
- 실패 건 재처리 및 최근 실행 시점/성공·실패 누적 카운트 관리
- 장애 로그 조회/필터 및 재처리 가능 여부 확인

## 2. 기술 스택
- Java 17
- Spring Boot 3.3.5
- Spring MVC + Thymeleaf
- Spring Data JPA
- H2 Database (In-Memory)
- Gradle
- JUnit 5, MockMvc

## 3. 아키텍처 및 패키지 구조
```
src/main/java/com/portfolio/integration
├─ controller
│  ├─ DashboardController.java
│  ├─ InterfaceApiController.java
│  └─ GlobalExceptionHandler.java
├─ domain
│  ├─ InsuranceInterface.java
│  ├─ ErrorLog.java
│  ├─ InsuranceInterfaceRepository.java
│  ├─ ErrorLogRepository.java
│  ├─ InterfaceStatus.java
│  ├─ InterfaceChannelType.java
│  └─ InterfaceDirection.java
├─ dto
│  ├─ DashboardMetrics.java
│  ├─ InterfaceRegistrationRequest.java
│  ├─ InterfaceUpdateRequest.java
│  ├─ InterfaceStatusUpdateRequest.java
│  ├─ InterfaceSearchCondition.java
│  ├─ InterfaceSummaryResponse.java
│  ├─ ErrorLogSearchCondition.java
│  └─ ErrorLogResponse.java
└─ service
	 └─ InterfaceMonitoringService.java
```

설계 의도:
- `domain`: 핵심 엔티티 및 Enum으로 운영 데이터 모델 정의
- `dto`: UI/API 요청·응답 계약 명확화
- `service`: 비즈니스 규칙(검색 조건 조합, 재처리, 상태 변경, 집계)
- `controller`: 웹 화면과 REST API를 분리하여 확장성 확보

## 4. 주요 기능

### 4.1 대시보드
- 총 인터페이스 수
- 정상/경고/장애/일시중지 건수
- 성공/실패 누적 건수
- 성공률
- 최근 장애/이벤트 로그
- 최근 실행 기준 인터페이스 현황

### 4.2 인터페이스 관리
- 등록
- 목록 조회
- 검색(코드, 이름, 출발 시스템, 대상 시스템, 담당팀)
- 상태 필터
- 채널 필터(REST_API, SOAP, MQ, BATCH, SFTP)
- 활성/비활성 필터
- 상세 조회
- 수정
- 비활성화(운영 종료)

### 4.3 운영 기능
- 실패 건 재처리
- 상태 변경(RUNNING, WARNING, FAILED, PAUSED)
- 최근 실행 시간 갱신
- 인터페이스별 성공/실패 누적 카운트 관리

### 4.4 장애/로그 관리
- 에러 로그 목록 조회
- 인터페이스별 로그 조회
- 재처리 가능 여부 표시
- 로그 검색/필터(키워드, interfaceId, retriable)

## 5. REST API

### 필수 API
- `GET /api/dashboard`
- `GET /api/interfaces`
- `GET /api/interfaces/{id}`
- `POST /api/interfaces`
- `PUT /api/interfaces/{id}`
- `POST /api/interfaces/{id}/retry`
- `GET /api/logs`

### 운영 확장 API
- `PUT /api/interfaces/{id}/status` : 상태 변경
- `POST /api/interfaces/{id}/deactivate` : 비활성화

## 6. 데이터 저장 및 초기 데이터
- H2 In-Memory DB 사용
- `data.sql` 자동 로딩으로 샘플 인터페이스/로그 데이터 구성
- JPA 엔티티 기반 영속화 적용

## 7. 화면
- `/` : 통합 대시보드 (지표 + 인터페이스 검색/필터 + 로그 필터)
- `/interfaces/{id}` : 인터페이스 상세/수정/상태 변경/재처리/로그 조회

## 8. 실행 방법

### 8.1 애플리케이션 실행
```bash
# Windows
.\gradlew.bat bootRun
```

접속 URL:
- `http://localhost:8081`
- H2 Console: `http://localhost:8081/h2-console`
	- JDBC URL: `jdbc:h2:mem:insurance_ifdb`
	- User: `sa`
	- Password: (빈 값)

### 8.2 테스트 실행
```bash
.\gradlew.bat test
```

## 9. 테스트 전략
- Service 테스트:
	- 대시보드 지표 계산 검증
	- 인터페이스 등록/검색 흐름 검증
	- 재처리 시 상태/카운트 변경 검증
- Controller 테스트(MockMvc):
	- 대시보드 API 응답 검증
	- 인터페이스 등록 API 및 목록 조회 검증
	- 재처리 API 응답 검증

## 10. 향후 개선점
- 운영 권한 관리(RBAC) 및 감사 로그
- 장애 등급/분류 체계 및 알림 연동(Slack, Email)
- 페이징/정렬/대량 작업 처리
- Prometheus/Grafana 연동 실시간 모니터링
- MySQL/PostgreSQL 프로파일 분리 및 Docker Compose 구성

## 11. 포트폴리오 포인트
- 금융권 운영 포털을 가정한 상태 기반 운영 UX
- 도메인/DTO/서비스/컨트롤러 계층 분리
- DB 영속화 + 샘플 데이터 + 테스트 코드 포함
- 실제 기업형 운영 요구(재처리/장애로그/상태전환)를 반영한 설계
