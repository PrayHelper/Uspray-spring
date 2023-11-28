package com.uspray.uspray.DTO.group.response;

import com.querydsl.core.annotations.QueryProjection;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Builder
@Schema(description = "모임 응답 DTO")
public class GroupResponseDto {

    private Long id;

    private String name;

    private String lastPrayContent;

    private Integer memberCount;

    private Integer prayCount;

    private LocalDateTime updatedAt;

    @QueryProjection
    public GroupResponseDto(Long id, String name, String lastPrayContent, Integer memberCount, Integer prayCount, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.lastPrayContent = lastPrayContent;
        this.memberCount = memberCount;
        this.prayCount = prayCount;
        this.updatedAt = updatedAt;
    }
}
