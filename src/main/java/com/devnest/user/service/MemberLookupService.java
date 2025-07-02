package com.devnest.user.service;


import com.devnest.user.domain.User;
import com.devnest.user.dto.response.EmailLookUpResponseDto;
import com.devnest.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberLookupService {

    private final UserRepository userRepository;

    public EmailLookUpResponseDto getUserInfoByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일을 가진 사용자가 없습니다."));
        return new EmailLookUpResponseDto(user.getNickname(), user.getCreatedAt());
    }
}
