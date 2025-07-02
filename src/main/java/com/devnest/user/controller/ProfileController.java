package com.devnest.user.controller;

import com.devnest.auth.domain.CustomUserDetails;
import com.devnest.user.domain.User;
import com.devnest.user.dto.common.NicknameRequestDto;
import com.devnest.user.dto.common.ProfileImageRequestDto;
import com.devnest.user.dto.response.ProfileResponseDto;
import com.devnest.user.repository.UserRepository;
import com.devnest.user.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequiredArgsConstructor
public class ProfileController {

  private final ProfileService profileService;
  private final UserRepository userRepository;

  // Controller
  @GetMapping("/member/profile")
  public String getProfile(Model model, Authentication authentication) {
    // 인증 안 된 경우 로그인 페이지로
    if (authentication == null || !authentication.isAuthenticated()) {
      return "redirect:/member/login";
    }

    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

    User user = userRepository.findById(userDetails.getUserId())
        .orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다."));

    model.addAttribute("profile", user);
    return "profile";
  }

  @GetMapping("/member/profile-info")
  @ResponseBody
  public ResponseEntity<ProfileResponseDto> getMyProfile(Authentication authentication) {
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    String email = userDetails.getEmail();
    String nickname = userDetails.getNickname();
    String profileImage = userDetails.getProfileImage();
    return ResponseEntity.ok(new ProfileResponseDto(email, nickname, profileImage));
  }

  // 닉네임 변경
  @PatchMapping("/member/update-nickname")
  public ResponseEntity<?> updateNickname(@RequestBody @Valid NicknameRequestDto dto,
      Authentication authentication) {
    Long userId = ((CustomUserDetails) authentication.getPrincipal()).getUserId();
    profileService.updateNickname(userId, dto.getNickname());

    // 최신 사용자 정보 가져오기
    User updatedUser = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

    // 새로운 UserDetails 생성
    CustomUserDetails newDetails = new CustomUserDetails(updatedUser);

    // Authentication 객체 재생성 및 SecurityContext에 주입
    Authentication newAuth = new UsernamePasswordAuthenticationToken(
        newDetails,
        authentication.getCredentials(),
        newDetails.getAuthorities()
    );

    SecurityContextHolder.getContext().setAuthentication(newAuth);

    return ResponseEntity.ok("닉네임이 성공적으로 변경되었습니다.");
  }

  // 프로필 이미지 업로드
  @PostMapping("/member/upload-profile-image")
  public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
    String uploadedPath = profileService.saveProfileImage(file);
    return ResponseEntity.ok(uploadedPath);
  }

  // 프로필 이미지 변경 경로 저장
  @PatchMapping("/member/update-profile-image")
  public ResponseEntity<?> updateProfileImage(@RequestBody @Valid ProfileImageRequestDto dto,
      Authentication authentication) {
    Long userId = ((CustomUserDetails) authentication.getPrincipal()).getUserId();
    profileService.updateProfileImage(userId, dto.getProfileImage());

    // 최신 사용자 정보 가져오기
    User updatedUser = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

    // 새로운 UserDetails 생성
    CustomUserDetails newDetails = new CustomUserDetails(updatedUser);

    // Authentication 객체 재생성 및 SecurityContext에 주입
    Authentication newAuth = new UsernamePasswordAuthenticationToken(
        newDetails,
        authentication.getCredentials(),
        newDetails.getAuthorities()
    );

    SecurityContextHolder.getContext().setAuthentication(newAuth);

    return ResponseEntity.ok("프로필 이미지가 성공적으로 변경되었습니다.");
  }
}
