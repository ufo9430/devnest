package com.devnest.user.util;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

@Component
public class EmailSender {

    private static final Logger log = LoggerFactory.getLogger(EmailSender.class);

    private static final Properties env = loadEnv();
    private static final String HOST = env.getProperty("SMTP_HOST");
    private static final String USER = env.getProperty("SMTP_USER");
    private static final String PASS = env.getProperty("SMTP_PASS");

    private static final Session session;

    static {
        if (HOST == null || USER == null || PASS == null) {
            log.warn("SMTP 환경변수가 없어 메일 전송 생략 (개발모드)");
            session = null;
        } else {
            Properties props = new Properties();
            props.put("mail.smtp.host", HOST);
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");

            session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(USER, PASS);
                }
            });
        }
    }

    public void sendCode(String to, String code) {
        if (session == null) {
            log.warn("메일 세션 없음 → 전송 생략: to={}, code={}", to, code);
            return;
        }
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(USER));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject("[DevNest] 인증 코드입니다");
            message.setText("[DevNest] 당신의 인증 코드는 " + code + " 입니다.\n10분 내에 입력해 주세요.");
            Transport.send(message);
            log.info("✅ 전송 완료 → {} (코드 = {})", to, code);
        } catch (MessagingException e) {
            log.error("메일 전송 실패", e);
        }
    }

    private static Properties loadEnv() {
        Properties props = new Properties();
        try (FileReader reader = new FileReader(".env")) {
            props.load(reader);
        } catch (IOException e) {
            log.warn(".env 로딩 실패: {}", e.getMessage());
        }
        return props;
    }

    public void sendTemporaryPassword(String to, String nickname, String tempPassword) {
        if (session == null) {
            log.warn("메일 세션 없음 → 전송 생략: to={}, tempPwd={}", to, tempPassword);
            return;
        }
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(USER));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject("[DevNest] 임시 비밀번호 안내");
            message.setText(String.format("""
            안녕하세요, %s님.
            요청하신 임시 비밀번호는 아래와 같습니다.
            
            임시 비밀번호: %s
            
            보안을 위해 로그인 후 반드시 비밀번호를 변경해 주세요.
            """, nickname, tempPassword));
            Transport.send(message);
            log.info("✅ 임시 비밀번호 전송 완료 → {} (비번 = {})", to, tempPassword);
        } catch (MessagingException e) {
            log.error("임시 비밀번호 메일 전송 실패", e);
        }
    }



}
