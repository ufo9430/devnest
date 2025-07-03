-- 테스트용 사용자 데이터
INSERT INTO user (user_id, email, nickname, password, role, is_active, created_at) VALUES 
(1, 'admin@test.com', '테스트관리자', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'ADMIN', true, NOW()),
(2, 'user1@test.com', '테스트사용자1', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'USER', true, NOW()),
(3, 'user2@test.com', '테스트사용자2', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'USER', true, NOW());

-- 테스트용 알림 데이터
INSERT INTO notification (notification_id, recipient_id, sender_id, type, message, title, target_type, target_id, is_read, created_at) VALUES 
(1, 2, 3, 'ANSWER', '새로운 답변이 등록되었습니다.', 'Spring Boot 테스트 질문', 'QUESTION', 101, false, NOW()),
(2, 2, 1, 'SYSTEM', '시스템 점검 안내입니다.', null, 'SYSTEM', null, false, NOW()),
(3, 3, 2, 'RECOMMEND', '답변이 추천되었습니다.', 'Java 기초 질문', 'ANSWER', 201, true, NOW()); 