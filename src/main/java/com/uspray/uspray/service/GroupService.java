package com.uspray.uspray.service;

import com.uspray.uspray.DTO.group.response.GroupListResponseDto;
import com.uspray.uspray.DTO.group.response.GroupMemberResponseDto;
import com.uspray.uspray.DTO.group.response.GroupResponseDto;
import com.uspray.uspray.infrastructure.GroupRepository;
import com.uspray.uspray.util.MaskingUtil;
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
    public List<GroupMemberResponseDto> searchGroupMembers(Long groupId, String name) {
        List<GroupMemberResponseDto> groupMemberResponseDtoList = groupRepository.findGroupMembersByGroupAndNameLike(groupId, name);
        for (GroupMemberResponseDto dto: groupMemberResponseDtoList) {
            dto.setUserId(MaskingUtil.maskUserId(dto.getUserId()));
        }
        return groupMemberResponseDtoList;
    }
}
