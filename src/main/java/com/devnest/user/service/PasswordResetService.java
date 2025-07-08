package com.devnest.user.service;


import com.devnest.user.domain.User;
import com.devnest.user.repository.UserRepository;
import com.devnest.user.util.EmailSender;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final UserRepository userRepository;
    private final EmailSender emailSender;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Transactional
    public String verifyAndResetPassword(String email, String code) {
        // ✅ EmailService의 verifyCode를 사용하여 인증 검증
        if (!emailService.verifyCode(email, code)) {
            throw new IllegalArgumentException("인증 코드가 일치하지 않거나 만료되었습니다.");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("입력하신 정보와 일치하는 계정을 찾을 수 없습니다."));

        String tempPassword = generateTempPassword(10);
        String encoded = passwordEncoder.encode(tempPassword);
        user.setPassword(encoded);
        userRepository.save(user);
        emailSender.sendTemporaryPassword(email, user.getNickname(), tempPassword);

        return tempPassword;
    }

    private String generateTempPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }




}
