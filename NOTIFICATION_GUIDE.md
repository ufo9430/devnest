# 📢 알림 시스템 사용 가이드

## 🚀 시작하기

### 1. 데이터베이스 테이블 생성
```sql
-- 프로젝트 실행 전에 MySQL에서 실행 필요
-- 파일: src/main/resources/notification_schema.sql 내용 실행
```

### 2. 기본 설정
- Spring Security가 활성화되어 있어 인증된 사용자만 접근 가능
- 관리자 전용 API는 ADMIN 권한 필요

## 📋 API 목록

### 👥 일반 사용자 API (인증 필요)

#### 알림 목록 조회
```bash
GET /api/notifications?userId=2&page=0&size=20
GET /api/notifications?userId=2&unreadOnly=true  # 읽지 않은 알림만
```

#### 알림 통계 조회
```bash
GET /api/notifications/stats?userId=2
```

#### 최근 알림 조회 (헤더용)
```bash
GET /api/notifications/recent?userId=2
```

#### 알림 읽음 처리
```bash
PATCH /api/notifications/1/read?userId=2              # 특정 알림
PATCH /api/notifications/read-all?userId=2            # 모든 알림
PATCH /api/notifications/read-selected?userId=2       # 선택한 알림들
Content-Type: application/json
Body: [1, 2, 3]  # 알림 ID 배열
```

### 👨‍💼 관리자 전용 API (ADMIN 권한 필요)

#### 시스템 알림 발송
```bash
POST /api/notifications/broadcast?message=전체공지메시지       # 전체 발송
POST /api/notifications/system?userId=2&message=개인메시지    # 개별 발송
```

#### 시스템 관리
```bash
DELETE /api/notifications/cleanup  # 오래된 알림 정리
```

## 🎨 프론트엔드 페이지

### 사용자 알림 페이지
```
http://localhost:8080/notifications.html
```

## 💾 데이터베이스 구조

### notification 테이블
- `notification_id`: 알림 ID (Primary Key)
- `recipient_id`: 받는 사용자 ID (Foreign Key → user.user_id)
- `sender_id`: 보낸 사용자 ID (Foreign Key → user.user_id, NULL 가능)
- `type`: 알림 유형 ('ANSWER', 'ACCEPT', 'RECOMMEND', 'SYSTEM')
- `message`: 알림 메시지
- `title`: 관련 게시물 제목
- `target_type`: 관련 엔티티 타입
- `target_id`: 관련 엔티티 ID
- `is_read`: 읽음 여부
- `created_at`: 생성 시간
- `read_at`: 읽은 시간

## 🔧 개발자 가이드

### 새로운 알림 타입 추가하기
1. `NotificationType.java`에 열거형 추가
2. `NotificationService.java`에 생성 메서드 추가
3. 해당 도메인에서 알림 서비스 호출

### 다른 도메인에서 알림 발송하기
```java
@Autowired
private NotificationService notificationService;

// 답변 알림 발송 예시
notificationService.createAnswerNotification(
    questionOwnerId,     // 받는 사용자
    answererUserId,      // 답변 작성자
    "새로운 답변이 등록되었습니다.",  // 메시지
    questionTitle,       // 질문 제목
    questionId          // 질문 ID
);
```

## 🛡️ 보안 설정

- 일반 알림 API: 인증된 사용자만 접근 가능
- 관리자 API: ADMIN 권한 필요 (`@PreAuthorize("hasRole('ADMIN')")`)
- 각 사용자는 자신의 알림만 조회/수정 가능

## 📝 주의사항

1. **테이블 생성**: 프로젝트 실행 전 `notification_schema.sql` 실행 필수
2. **권한 체크**: API 호출 시 적절한 사용자 권한 확인
3. **성능 최적화**: 대량의 알림 처리 시 페이징 사용 권장
4. **정리 작업**: 주기적으로 `/cleanup` API 호출하여 오래된 알림 정리

---

## �� 기여자
- 알림 시스템 개발팀 