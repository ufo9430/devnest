package com.devnest.global.config;

import com.devnest.domain.notification.entity.Notification;
import com.devnest.domain.notification.entity.NotificationType;
import com.devnest.domain.notification.repository.NotificationRepository;
import com.devnest.user.domain.Role;
import com.devnest.user.domain.User;
import com.devnest.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            log.info("🚀 테스트 데이터 생성 시작...");
            createTestUsers();
            createTestNotifications();
            log.info("✅ 테스트 데이터 생성 완료!");
        } else {
            log.info("📋 기존 데이터가 존재하여 테스트 데이터 생성을 건너뜁니다.");
        }
    }

    private void createTestUsers() {
        // 테스트용 관리자 사용자
        User admin = User.builder()
                .email("admin@devnest.com")
                .nickname("관리자")
                .password(passwordEncoder.encode("admin123"))
                .role(Role.ADMIN)
                .isActive(true)
                .createdAt(LocalDateTime.now().minusDays(10))
                .build();
        userRepository.save(admin);

        // 테스트용 일반 사용자들
        String[] nicknames = { "개발자123", "백엔드마스터", "프론트엔드킹", "풀스택개발자", "테스터01" };
        String[] emails = { "dev123@test.com", "backend@test.com", "frontend@test.com", "fullstack@test.com",
                "tester@test.com" };

        for (int i = 0; i < nicknames.length; i++) {
            User user = User.builder()
                    .email(emails[i])
                    .nickname(nicknames[i])
                    .password(passwordEncoder.encode("test123"))
                    .role(Role.USER)
                    .isActive(i < 4) // 마지막 사용자는 비활성 상태로
                    .createdAt(LocalDateTime.now().minusDays(5 - i))
                    .build();
            userRepository.save(user);
        }

        log.info("👥 총 {}명의 테스트 사용자를 생성했습니다.", nicknames.length + 1);
    }

    private void createTestNotifications() {
        // 사용자들 조회
        User recipient = userRepository.findById(1L).orElse(null);
        User sender = userRepository.findById(2L).orElse(null);

        if (recipient == null) {
            log.warn("알림 수신자를 찾을 수 없습니다.");
            return;
        }

        // 다양한 타입의 테스트 알림들
        String[] messages = {
                "새로운 답변이 등록되었습니다.",
                "회원님의 답변이 채택되었습니다!",
                "게시글에 좋아요가 추가되었습니다.",
                "새로운 댓글이 달렸습니다.",
                "회원님이 멘션되었습니다.",
                "새로운 팔로워가 있습니다.",
                "시스템 점검 안내입니다.",
                "환영합니다! DevNest에 가입해주셔서 감사합니다.",
                "프로필 사진이 업데이트되었습니다.",
                "비밀번호가 성공적으로 변경되었습니다."
        };

        NotificationType[] types = {
                NotificationType.ANSWER,
                NotificationType.ACCEPT,
                NotificationType.RECOMMEND,
                NotificationType.COMMENT,
                NotificationType.MENTION,
                NotificationType.FOLLOW,
                NotificationType.SYSTEM,
                NotificationType.SYSTEM,
                NotificationType.SYSTEM,
                NotificationType.SYSTEM
        };

        for (int i = 0; i < messages.length; i++) {
            Notification notification = Notification.builder()
                    .type(types[i])
                    .title("알림")
                    .message(messages[i])
                    .recipient(recipient)
                    .sender(i % 2 == 0 ? sender : null) // 일부는 발신자 있음
                    .targetId((long) (i + 1))
                    .targetType("POST")
                    .isRead(i > 6) // 처음 7개는 읽지 않음
                    .createdAt(LocalDateTime.now().minusHours(i * 2))
                    .readAt(i > 6 ? LocalDateTime.now().minusHours(i) : null)
                    .build();

            notificationRepository.save(notification);
        }

        log.info("🔔 총 {}개의 테스트 알림을 생성했습니다.", messages.length);
    }
}