# Spring Batch Study Project

이 프로젝트는 Spring Batch의 핵심 개념과 실무 적용 사례를 학습하기 위한 스터디 프로젝트입니다.

## 1. Spring Batch 선택 이유

### 기술 선택 기준
- 대용량 데이터 처리에 최적화된 프레임워크
- Spring 생태계와의 완벽한 통합
- 풍부한 기능과 높은 확장성
- 재시작, 재시도, 건너뛰기 등 오류 처리 기능 제공

### 장점
- 트랜잭션 관리, 청크 기반 처리, 재시도 로직 등 배치 처리에 필요한 기능 기본 제공
- Spring의 DI, AOP 등 핵심 기능 활용 가능
- 다양한 Reader와 Writer 제공으로 데이터 처리 유연성 확보

### 단점
- 학습 곡선이 존재
- 메타테이블 의존성
- 단순 배치의 경우 오버엔지니어링 가능성

## 2. 멱등성 유지 방법

- 고유 Job Parameter 사용으로 중복 실행 방지
- ItemProcessor에서 데이터 검증 및 필터링
- 업데이트 작업시 상태 체크 로직 구현
- 트랜잭션 관리를 통한 데이터 일관성 유지

## 3. Spring Batch 메타데이터 테이블

주요 테이블:
- `BATCH_JOB_INSTANCE`: 작업 실행 정보
- `BATCH_JOB_EXECUTION`: 작업 실행 결과
- `BATCH_JOB_EXECUTION_PARAMS`: 작업 파라미터
- `BATCH_STEP_EXECUTION`: 스텝 실행 정보
- `BATCH_STEP_EXECUTION_CONTEXT`: 스텝 실행 컨텍스트

## 4. 배치 실패 처리

### Skip
- 특정 예외 발생 시 해당 아이템을 건너뛰고 계속 진행
- `skipLimit`과 `skippableExceptionClasses` 설정으로 관리
```kotlin
.faultTolerant()
.skip(Exception::class.java)
.skipLimit(3)
```

### Retry
- 일시적 문제로 인한 실패 시 재시도
- `retryLimit`과 `retryableExceptionClasses` 설정
```kotlin
.faultTolerant()
.retry(Exception::class.java)
.retryLimit(3)
```

## 5. 멀티스레드 처리 방식

### 멀티스레드 Step
- 단일 Step 내에서 멀티스레드로 청크 처리
- `TaskExecutor` 설정으로 구현
```kotlin
.taskExecutor(taskExecutor)
.throttleLimit(4)
```

### 파티셔닝
- 데이터를 파티션으로 나누어 병렬 처리
- Master/Slave 구조로 작업 분배
- 데이터 격리가 보장되어 동시성 이슈 감소

차이점:
- 멀티스레드: 단일 Step 내 병렬 처리
- 파티셔닝: 데이터 분할 후 독립적인 Step으로 처리

## 6. 청크 기반 트랜잭션

청크 단위 트랜잭션 처리 이유:
- 메모리 사용량 최적화
- 장애 발생 시 롤백 범위 최소화
- 데이터 처리의 안정성 향상
- 처리 진행 상황 추적 용이

## 7. Tasklet vs Reader/Writer

### Tasklet
- 단순한 작업에 적합
- 한 번에 전체 로직 처리
- 커스텀 로직 구현 용이

### Reader/Writer
- 대용량 데이터 처리에 적합
- 청크 단위 처리 가능
- 트랜잭션 관리 용이
- 재시작 지점 관리 가능

## 8. 커서 vs 페이징

### 커서 방식
- JdbcCursorItemReader
- 데이터베이스 커서 사용
- 메모리 효율적
- 긴 DB 연결 시간 필요

### 페이징 방식
- JdbcPagingItemReader
- 페이지 단위로 조회
- 짧은 DB 연결로 충분
- 데이터 정렬 필요

## 9. 배치 실행 관리

Jenkins를 통한 관리:
- Cron 표현식으로 스케줄링
- 실행 이력 관리
- 파라미터 설정 및 관리
- 실패 알림 설정

## 10. 모니터링

모니터링 방법:
- Spring Batch Admin
- Actuator 엔드포인트 활용
- 메타테이블 쿼리
- 로그 모니터링
- 알림 설정 (Slack, Email 등)

지연 배치 처리:
- 임계치 설정
- 알림 자동화
- 대체 실행 계획 수립
- 로그 분석을 통한 원인 파악

## 기술 스택

- Kotlin
- Spring Boot 3.3.6
- Spring Batch
- Gradle
- JDK 21

## 프로젝트 실행

```bash
./gradlew bootRun
```

## 테스트 실행

```bash
./gradlew test
```
