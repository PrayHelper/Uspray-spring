package com.uspray.uspray.DTO.auth.request;

import com.uspray.uspray.global.enums.Authority;
import com.uspray.uspray.domain.member.model.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.validation.constraints.NotBlank;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberRequestDto {

    @NotBlank
    @Schema(description = "ID", example = "test")
    private String userId;

    @NotBlank
    @Schema(description = "비밀번호", example = "test")
    private String password;

    @NotBlank
    @Schema(description = "이름", example = "홍길동")
    private String name;

    @NotBlank
    @Schema(description = "전화번호", example = "01012345678")
    private String phone;

    @Schema(description = "생년월일", example = "2002-01-01")
    private String birth;

    @Schema(description = "성별", example = "female")
    private String gender;

    public Member toMember(PasswordEncoder passwordEncoder) {
        return Member.builder()
                .userId(userId)
                .password(passwordEncoder.encode(password))
                .name(name)
                .phone(phone)
                .birth(birth)
                .gender(gender)
                .authority(Authority.ROLE_USER)
                .build();
    }
}
