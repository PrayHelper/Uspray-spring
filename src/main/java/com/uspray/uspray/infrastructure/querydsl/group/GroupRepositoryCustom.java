package com.uspray.uspray.infrastructure.querydsl.group;

import com.uspray.uspray.DTO.group.response.GroupMemberResponseDto;
import com.uspray.uspray.DTO.group.response.GroupResponseDto;

import java.util.List;

public interface GroupRepositoryCustom {

    List<GroupResponseDto> findGroupListByMemberId(String userId);

    List<GroupMemberResponseDto> findGroupMembersByGroupAndNameLike(Long groupId, String name);

}
