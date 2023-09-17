package com.uspray.uspray.controller.dto.response;

import com.uspray.uspray.controller.dto.request.MemberRequestDto;
import com.uspray.uspray.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberResponseDto {

    private String userId;
    private String name;
    private String phoneNum;

    public static MemberResponseDto of(Member member) {
        return new MemberResponseDto(member.getUserId(), member.getName(), member.getPhoneNum());
    }
}
