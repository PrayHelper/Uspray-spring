package com.uspray.uspray.domain.group.repository.querydsl;

import com.uspray.uspray.domain.group.dto.group.response.GroupMemberResponseDto;
import com.uspray.uspray.domain.group.dto.group.response.GroupResponseDto;
import java.util.List;

public interface GroupRepositoryCustom {

    List<GroupResponseDto> findGroupListByMemberId(String userId);

    List<GroupMemberResponseDto> findGroupMembersByGroupAndNameLikeExceptUser(Long groupId,
        String name, String username);

}
