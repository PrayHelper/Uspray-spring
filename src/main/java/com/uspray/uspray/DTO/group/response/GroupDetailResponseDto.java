package com.uspray.uspray.DTO.group.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GroupDetailResponseDto {

    private Long id;

    private String name;

    private String description;

    private String leader;

    private String memberCount;
}
