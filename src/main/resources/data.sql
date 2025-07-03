-- 관리자 계정 생성 (비밀번호: admin123)
INSERT INTO users (user_id, email, nickname, password, role, is_active, created_at) 
VALUES (1, 'admin@devnest.com', '관리자', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'ADMIN', true, NOW());

-- 일반 사용자들 생성
INSERT INTO users (user_id, email, nickname, password, role, is_active, created_at) 
VALUES 
    (2, 'user1@test.com', '개발자123', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'USER', true, NOW()),
    (3, 'user2@test.com', '백엔드마스터', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'USER', true, NOW()),
    (4, 'user3@test.com', 'Vue개발자', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'USER', true, NOW()),
    (5, 'user4@test.com', '타입러버', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'USER', true, NOW()),
    (6, 'user5@test.com', '스프링신입', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'USER', true, NOW());

-- 알림 데이터 생성
INSERT INTO notification (notification_id, recipient_id, sender_id, type, message, title, target_type, target_id, is_read, created_at) 
VALUES 
    -- 개발자123(2)의 알림들
    (1, 2, 3, 'ANSWER', '백엔드마스터가 새로운 답변을 달았습니다.', 'React Hook에서 useEffect 의존성 배열 관련 질문입니다', 'QUESTION', 101, false, DATE_SUB(NOW(), INTERVAL 5 MINUTE)),
    (2, 2, null, 'ACCEPT', '회원님의 답변이 채택되었습니다.', 'JavaScript 클로저와 스코프 완벽 이해하기', 'ANSWER', 201, false, DATE_SUB(NOW(), INTERVAL 1 HOUR)),
    (3, 2, 4, 'RECOMMEND', '회원님의 답변이 추천되었습니다.', 'Python 리스트 컴프리헨션 최적화 방법', 'ANSWER', 202, true, DATE_SUB(NOW(), INTERVAL 3 HOUR)),
    (4, 2, 5, 'ANSWER', '타입러버가 새로운 답변을 달았습니다.', 'CSS Grid와 Flexbox 언제 사용해야 할까요?', 'QUESTION', 102, true, DATE_SUB(NOW(), INTERVAL 6 HOUR)),
    (5, 2, null, 'ACCEPT', '회원님의 답변이 채택되었습니다.', 'Node.js Express 미들웨어 작동 원리', 'ANSWER', 203, true, DATE_SUB(NOW(), INTERVAL 1 DAY)),
    
    -- 백엔드마스터(3)의 알림들
    (6, 3, 2, 'ANSWER', '개발자123가 새로운 답변을 달았습니다.', 'Spring Boot JPA 연관관계 매핑 질문', 'QUESTION', 103, false, DATE_SUB(NOW(), INTERVAL 2 HOUR)),
    (7, 3, null, 'ACCEPT', '회원님의 답변이 채택되었습니다.', 'MySQL 인덱스 최적화 방법', 'ANSWER', 204, true, DATE_SUB(NOW(), INTERVAL 4 HOUR)),
    (8, 3, 6, 'RECOMMEND', '회원님의 답변이 추천되었습니다.', 'Docker 컨테이너 배포 전략', 'ANSWER', 205, true, DATE_SUB(NOW(), INTERVAL 1 DAY)),
    
    -- Vue개발자(4)의 알림들
    (9, 4, 5, 'ANSWER', '타입러버가 새로운 답변을 달았습니다.', 'Vue 3 Composition API 활용법', 'QUESTION', 104, false, DATE_SUB(NOW(), INTERVAL 30 MINUTE)),
    (10, 4, null, 'ACCEPT', '회원님의 답변이 채택되었습니다.', 'TypeScript 제네릭 고급 활용', 'ANSWER', 206, false, DATE_SUB(NOW(), INTERVAL 2 HOUR)),
    
    -- 시스템 알림 (모든 사용자에게)
    (11, 2, null, 'SYSTEM', '새로운 기능이 업데이트되었습니다. 알림 시스템을 확인해보세요!', null, 'SYSTEM', null, false, DATE_SUB(NOW(), INTERVAL 10 MINUTE)),
    (12, 3, null, 'SYSTEM', '새로운 기능이 업데이트되었습니다. 알림 시스템을 확인해보세요!', null, 'SYSTEM', null, true, DATE_SUB(NOW(), INTERVAL 10 MINUTE)),
    (13, 4, null, 'SYSTEM', '새로운 기능이 업데이트되었습니다. 알림 시스템을 확인해보세요!', null, 'SYSTEM', null, false, DATE_SUB(NOW(), INTERVAL 10 MINUTE)),
    (14, 5, null, 'SYSTEM', '새로운 기능이 업데이트되었습니다. 알림 시스템을 확인해보세요!', null, 'SYSTEM', null, true, DATE_SUB(NOW(), INTERVAL 10 MINUTE)),
    (15, 6, null, 'SYSTEM', '새로운 기능이 업데이트되었습니다. 알림 시스템을 확인해보세요!', null, 'SYSTEM', null, false, DATE_SUB(NOW(), INTERVAL 10 MINUTE)); 