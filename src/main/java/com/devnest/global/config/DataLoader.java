package com.devnest.global.config;

import com.devnest.notice.entity.Notification;
import com.devnest.notice.entity.NotificationType;
import com.devnest.notice.repository.NotificationRepository;
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
import java.util.ArrayList;
import java.util.List;

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
        }
    }

    private void createTestUsers() {
        List<User> users = new ArrayList<>();

        // 관리자 계정
        users.add(User.builder()
                .email("admin@devnest.com")
                .nickname("관리자")
                .password(passwordEncoder.encode("admin123"))
                .role(Role.ADMIN)
                .isActive(true)
                .profileImage("/images/default_user_profile.png")
                .createdAt(LocalDateTime.now().minusDays(100))
                .build());

        // 일반 사용자들 (19명)
        String[] nicknames = {
                "개발자123", "백엔드마스터", "프론트엔드러버", "타입러버", "자바킹",
                "리액트마니아", "스프링부트전문가", "데이터베이스구루", "알고리즘마스터", "웹개발자",
                "풀스택개발자", "UI/UX디자이너", "모바일개발자", "게임개발자", "AI연구원",
                "데브옵스엔지니어", "클라우드전문가", "보안전문가", "QA테스터"
        };

        String[] emailDomains = { "gmail.com", "naver.com", "kakao.com", "devnest.com", "example.com" };

        for (int i = 0; i < nicknames.length; i++) {
            users.add(User.builder()
                    .email(nicknames[i].toLowerCase() + "@" + emailDomains[i % emailDomains.length])
                    .nickname(nicknames[i])
                    .password(passwordEncoder.encode("password123"))
                    .role(Role.USER)
                    .isActive(i % 6 != 0) // 일부 사용자를 비활성화
                    .profileImage("/images/default_user_profile.png")
                    .createdAt(LocalDateTime.now().minusDays(90 - i * 2))
                    .build());
        }

        userRepository.saveAll(users);
        log.info("👥 총 {}명의 테스트 사용자를 생성했습니다.", users.size());
    }

    private void createTestNotifications() {
        List<User> users = userRepository.findAll();
        if (users.size() < 4)
            return;

        List<Notification> notifications = new ArrayList<>();

        // 다양한 알림 생성
        String[] messages = {
                "새로운 답변이 달렸습니다.",
                "회원님의 답변이 채택되었습니다.",
                "회원님의 답변이 추천되었습니다.",
                "새로운 댓글이 달렸습니다.",
                "회원님을 팔로우하기 시작했습니다.",
                "새로운 시스템 업데이트가 있습니다.",
                "회원님의 질문에 새로운 답변이 달렸습니다.",
                "추천받은 답변이 있습니다.",
                "새로운 멘션이 있습니다.",
                "중요한 공지사항이 있습니다."
        };

        String[] titles = {
                "React Hook 사용법 관련 질문",
                "JavaScript 클로저 완벽 이해하기",
                "Python 리스트 컴프리헨션 최적화",
                "CSS Grid vs Flexbox 비교",
                "Node.js Express 미들웨어",
                "Spring Boot JPA 연관관계",
                "MySQL 인덱스 최적화",
                "Docker 컨테이너 배포",
                "Vue 3 Composition API",
                "TypeScript 제네릭 활용"
        };

        NotificationType[] types = {
                NotificationType.ANSWER, NotificationType.ACCEPT, NotificationType.RECOMMEND,
                NotificationType.COMMENT, NotificationType.FOLLOW, NotificationType.SYSTEM
        };

        for (int i = 0; i < 10; i++) {
            User recipient = users.get((i % (users.size() - 1)) + 1); // 관리자 제외
            User sender = i % 3 == 0 ? null : users.get(i % (users.size() - 1) + 1);

            notifications.add(Notification.builder()
                    .recipient(recipient)
                    .sender(sender)
                    .type(types[i % types.length])
                    .message(messages[i % messages.length])
                    .title(titles[i % titles.length])
                    .targetType("QUESTION")
                    .targetId((long) (100 + i))
                    .isRead(i % 3 == 0)
                    .createdAt(LocalDateTime.now().minusHours(i * 2))
                    .build());
        }

        notificationRepository.saveAll(notifications);
        log.info("🔔 총 {}개의 테스트 알림을 생성했습니다.", notifications.size());
    }
}