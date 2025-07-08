package com.devnest.user.service;

import com.devnest.user.domain.User;
import com.devnest.user.repository.UserRepository;
import java.io.File;
import java.io.IOException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ProfileService {

  private final UserRepository userRepository;

  public void updateNickname(Long userId, String newNickname) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
    user.setNickname(newNickname);
    userRepository.save(user);
  }

  public String saveProfileImage(MultipartFile file) {
    String filename = "profile_" + UUID.randomUUID() + "." +
        StringUtils.getFilenameExtension(file.getOriginalFilename());

    // 외부 디렉토리
    String uploadDir = System.getProperty("user.dir") + "/uploads/images/";
    File dir = new File(uploadDir);
    if (!dir.exists()) {
      dir.mkdirs(); // 디렉토리 없으면 생성
    }

    File dest = new File(dir, filename);

    try {
      file.transferTo(dest);
    } catch (IOException e) {
      throw new RuntimeException("파일 저장 실패", e);
    }

    return "/images/" + filename; // 웹에서 접근 가능한 경로로 반환
  }

  public void updateProfileImage(Long userId, String newProfileImage) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
    user.setProfileImage(newProfileImage);
    userRepository.save(user);
  }
}
