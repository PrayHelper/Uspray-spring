package com.uspray.uspray.service;

import com.uspray.uspray.DTO.auth.response.MemberResponseDto;
import com.uspray.uspray.DTO.group.response.GroupListResponseDto;
import com.uspray.uspray.DTO.group.response.GroupResponseDto;
import com.uspray.uspray.infrastructure.GroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;

    @Transactional(readOnly = true)
    public GroupListResponseDto getGroupList(String username) {
        List<GroupResponseDto> groupList = groupRepository.findGroupListByMemberId(username);
        return new GroupListResponseDto(groupList);
    }

    @Transactional(readOnly = true)
    public List<MemberResponseDto> searchGroupMembers(Long groupId, String name) {
        return groupRepository.findGroupMembersByGroupAndNameLike(groupId, name);
    }
}
