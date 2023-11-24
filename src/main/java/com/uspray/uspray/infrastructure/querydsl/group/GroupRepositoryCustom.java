package com.uspray.uspray.infrastructure.querydsl.group;

import com.uspray.uspray.DTO.group.response.GroupResponseDto;
import com.uspray.uspray.domain.Group;
import com.uspray.uspray.domain.Member;

import java.time.LocalDateTime;
import java.util.List;

public interface GroupRepositoryCustom {

    List<GroupResponseDto> findGroupListByMember(Member member);
}
