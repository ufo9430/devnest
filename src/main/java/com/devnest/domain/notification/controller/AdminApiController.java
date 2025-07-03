package com.devnest.domain.notification.controller;

import com.devnest.user.domain.User;
import com.devnest.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin/api")
// @PreAuthorize("hasRole('ADMIN')") // 임시 주석 처리
public class AdminApiController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/users")
    public ResponseEntity<Map<String, Object>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String search) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<User> users;

        if (search == null || search.trim().isEmpty()) {
            users = userRepository.findAll(pageable);
        } else {
            users = userRepository.findByEmailContainingIgnoreCaseOrNicknameContainingIgnoreCase(
                    search.trim(), search.trim(), pageable);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("message", "사용자 목록 조회 성공");
        response.put("data", users);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/users/stats")
    public ResponseEntity<Map<String, Object>> getUserStats() {
        Map<String, Object> stats = new HashMap<>();

        long totalUsers = userRepository.count();
        long activeUsers = userRepository.countByIsActive(true);
        long inactiveUsers = userRepository.countByIsActive(false);
        long adminUsers = userRepository.countByRole(com.devnest.user.domain.Role.ADMIN);

        stats.put("totalUsers", totalUsers);
        stats.put("activeUsers", activeUsers);
        stats.put("inactiveUsers", inactiveUsers);
        stats.put("adminUsers", adminUsers);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "사용자 통계 조회 성공");
        response.put("data", stats);

        return ResponseEntity.ok(response);
    }
}