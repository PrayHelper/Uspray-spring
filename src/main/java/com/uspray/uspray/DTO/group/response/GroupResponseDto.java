package com.uspray.uspray.DTO.group.response;

import com.uspray.uspray.domain.Group;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "모임 응답 DTO")
public class GroupResponseDto {

    private Long id;

    private String name;

    private String lastPrayContent;

    private Integer memberCount;

    private Integer prayCount;

    private LocalDateTime updatedAt;

}
