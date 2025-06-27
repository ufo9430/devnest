package com.devnest.user.dto.request;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequestDto {

//    @NotBlank(message = "이메일은 필수입니다.")
//    @Email(message = "유효한 이메일 형식이어야 합니다.")
//    private String email;
    // 이메일 단독으로 분리하겠습니다 확인 부탁드리겠습니다 ㅠㅠ

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하로 입력해주세요.")
    @Pattern(regexp = "^[A-Za-z\\d~!@#$%^&*()_+\\-=\\[\\]{},.<>?/]{8,}$",
            message = "비밀번호는 8자 이상이며 허용된 문자만 사용할 수 있습니다.") //채린님 의견 통합!
    //뷰에 요거 패턴대로 입력하고 써져 있나요? 없으면 추가 요청 해야할것 같아요!
    private String password;

    @NotBlank(message = "닉네임은 필수입니다.")
    @Size(max = 50, message = "닉네임은 50자 이하로 입력해주세요.")
    private String nickname;
}
