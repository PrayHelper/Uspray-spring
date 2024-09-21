package com.uspray.uspray.domain.group.dto.group.response;

import com.querydsl.core.annotations.QueryProjection;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class GroupMemberResponseDto {

    @Schema(description = "ID", example = "1")
    private Long id;

    @Schema(description = "USER ID", example = "test")
    private String userId;

    @Schema(description = "USER NAME", example = "홍길동")
    private String name;

    @QueryProjection
    public GroupMemberResponseDto(Long id, String userId, String name) {
        this.id = id;
        this.userId = userId;
        this.name = name;
    }
}
