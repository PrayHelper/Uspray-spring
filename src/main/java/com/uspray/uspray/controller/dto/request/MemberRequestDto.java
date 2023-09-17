package com.uspray.uspray.controller.dto.request;

import com.uspray.uspray.domain.Authority;
import com.uspray.uspray.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberRequestDto {

    @NotBlank
    private String userId;

    @NotBlank
    private String password;

    @NotBlank
    private String name;

    @NotBlank
    private String phoneNum;

    @NotBlank
    private String birth;

    @NotBlank
    private String gender;

    public Member toMember(PasswordEncoder passwordEncoder) {
        return Member.builder()
                .userId(userId)
                .password(passwordEncoder.encode(password))
                .name(name)
                .phoneNum(phoneNum)
                .birth(birth)
                .gender(gender)
                .authority(Authority.ROLE_USER)
                .build();
    }
}
