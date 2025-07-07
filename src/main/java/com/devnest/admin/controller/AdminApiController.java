package com.devnest.admin.controller;

import com.devnest.user.domain.User;
import com.devnest.user.domain.Role;
import com.devnest.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/api")
public class AdminApiController {

    private static final Logger log = LoggerFactory.getLogger(AdminApiController.class);
    private final UserRepository userRepository;

    public AdminApiController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/users/stats")
    public ResponseEntity<Map<String, Object>> getUserStats() {
        log.info("📊 사용자 통계 요청");

        long totalUsers = userRepository.count();
        long activeUsers = userRepository.countByIsActive(true);
        long inactiveUsers = userRepository.countByIsActive(false);
        long adminUsers = userRepository.countByRole(Role.ADMIN);

        Map<String, Object> result = new HashMap<>();
        Map<String, Object> data = new HashMap<>();
        data.put("totalUsers", totalUsers);
        data.put("activeUsers", activeUsers);
        data.put("inactiveUsers", inactiveUsers);
        data.put("adminUsers", adminUsers);
        result.put("data", data);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/users")
    public ResponseEntity<Map<String, Object>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String search) {
        log.info("👥 사용자 목록 요청 - page: {}, size: {}, search: {}", page, size, search);
        Pageable pageable = PageRequest.of(page, size);
        Page<User> users;
        if (search.isEmpty()) {
            users = userRepository.findAll(pageable);
        } else {
            users = userRepository.findByEmailContainingIgnoreCaseOrNicknameContainingIgnoreCase(search, search,
                    pageable);
        }
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> data = new HashMap<>();
        data.put("content", users.getContent().stream().map(user -> {
            Map<String, Object> u = new HashMap<>();
            u.put("id", user.getUserId());
            u.put("email", user.getEmail());
            u.put("nickname", user.getNickname());
            u.put("role", user.getRole());
            u.put("isActive", user.getIsActive());
            u.put("createdAt", user.getCreatedAt());
            return u;
        }).collect(Collectors.toList()));
        data.put("totalElements", users.getTotalElements());
        data.put("totalPages", users.getTotalPages());
        data.put("number", users.getNumber());
        data.put("size", users.getSize());
        data.put("first", users.isFirst());
        data.put("last", users.isLast());
        result.put("data", data);
        return ResponseEntity.ok(result);
    }
}