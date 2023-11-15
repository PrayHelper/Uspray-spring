package com.uspray.uspray.DTO.group.response;

import com.uspray.uspray.domain.Group;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "모임 응답 DTO")
public class GroupResponseDto {

    private Group group;

    private Boolean isLeader;

}
