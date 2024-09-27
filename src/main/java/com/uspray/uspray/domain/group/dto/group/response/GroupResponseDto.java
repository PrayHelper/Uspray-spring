package com.uspray.uspray.domain.group.dto.group.response;

import com.querydsl.core.annotations.QueryProjection;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    private boolean isLeader;

    @QueryProjection
    public GroupResponseDto(Long id, String name, String lastPrayContent, Integer memberCount,
        Integer prayCount, LocalDateTime updatedAt, boolean isLeader) {
        this.id = id;
        this.name = name;
        this.lastPrayContent = Optional.ofNullable(lastPrayContent).isPresent() ? new String(
            Base64.getDecoder().decode(lastPrayContent)) : null;
        this.memberCount = memberCount;
        this.prayCount = prayCount;
        this.updatedAt = updatedAt;
        this.isLeader = isLeader;
    }
}
