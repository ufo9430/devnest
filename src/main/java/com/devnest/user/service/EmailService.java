package com.devnest.user.service;

import com.devnest.user.util.EmailSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final EmailSender emailSender;

    // 인증코드 저장소 (email -> code)
    private final Map<String, String> verificationCodes = new ConcurrentHashMap<>();

    public void sendVerificationCode(String to) {
        String code = generateCode();  // 인증코드 생성
        verificationCodes.put(to, code);//저장소에 put
        emailSender.sendCode(to, code); //코드 전송
    }
   //코드 인증
    public boolean verifyCode(String email, String code) {
        String stored = verificationCodes.get(email);
        if (stored != null && stored.equals(code)) {
            verificationCodes.remove(email); // 일회성 사용
            return true;
        }
        return false;
    }

    private String generateCode() {
        int length = 6;
        StringBuilder code = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            code.append(random.nextInt(10));  // 0~9 숫자
        }
        return code.toString();
    }
}
