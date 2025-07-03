-- 알림 시스템 테이블 생성 스크립트
-- ddl-auto: none 설정으로 인해 수동으로 실행 필요

-- notification 테이블 생성
CREATE TABLE IF NOT EXISTS notification (
    notification_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    recipient_id BIGINT NOT NULL,
    sender_id BIGINT,
    type VARCHAR(20) NOT NULL,
    message VARCHAR(500) NOT NULL,
    title VARCHAR(200),
    target_type VARCHAR(50),
    target_id BIGINT,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    read_at DATETIME,
    
    FOREIGN KEY (recipient_id) REFERENCES user(user_id) ON DELETE CASCADE,
    FOREIGN KEY (sender_id) REFERENCES user(user_id) ON DELETE SET NULL,
    
    INDEX idx_recipient_created (recipient_id, created_at DESC),
    INDEX idx_recipient_unread (recipient_id, is_read),
    INDEX idx_type (type),
    INDEX idx_created_at (created_at)
);

-- notification_type 열거형 값들: 
-- 'ANSWER', 'ACCEPT', 'RECOMMEND', 'SYSTEM' 