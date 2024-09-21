package com.uspray.uspray.DTO.auth.response;

import com.querydsl.core.annotations.QueryProjection;
import com.uspray.uspray.domain.member.model.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberResponseDto {

    @Schema(description = "ID", example = "test")
    private String userId;

    @Schema(description = "이름", example = "홍길동")
    private String name;

    @Schema(description = "전화번호", example = "01012345678")
    private String phone;

    @QueryProjection
    public MemberResponseDto(String userId, String name, String phone){
        this.userId = userId;
        this.name = name;
        this.phone = phone;
    }

    public static MemberResponseDto of(Member member) {
        return new MemberResponseDto(member.getUserId(), member.getName(), member.getPhone());
    }
}
