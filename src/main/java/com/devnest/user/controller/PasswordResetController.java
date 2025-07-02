package com.devnest.user.controller;


import com.devnest.user.service.PasswordResetService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

//@RestController
//@RequestMapping("/member")
//@RequiredArgsConstructor
//public class PasswordResetController {
//
//    private final PasswordResetService passwordResetService;
//
//
//    @PostMapping("/reset-password")
//    public String resetPassword(
////            @RequestParam @Email String email,
////            @RequestParam String code
//            @RequestBody @Email String email,
//            @RequestBody String code
//    )
//    {
//        return passwordResetService.verifyAndResetPassword(email, code);
//    }
//
//}
@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
@Validated
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    @PostMapping("/reset-password")
    public String resetPassword(@RequestBody @Valid ResetPasswordRequest request) {
        return passwordResetService.verifyAndResetPassword(request.getEmail(), request.getCode());
    }

    @Getter
    @Setter
    public static class ResetPasswordRequest {
        @Email
        private String email;
        private String code;
    }
}

