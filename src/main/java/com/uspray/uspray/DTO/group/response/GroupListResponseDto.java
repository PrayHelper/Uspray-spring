package com.uspray.uspray.DTO.group.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GroupListResponseDto {

    List<GroupResponseDto> groupList;
}
