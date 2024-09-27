package com.uspray.uspray.domain.group.service;

import com.uspray.uspray.domain.group.dto.group.response.GroupListResponseDto;
import com.uspray.uspray.domain.group.dto.group.response.GroupMemberResponseDto;
import com.uspray.uspray.domain.group.dto.group.response.GroupResponseDto;
import com.uspray.uspray.domain.group.repository.GroupRepository;
import com.uspray.uspray.global.util.MaskingUtil;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public List<GroupMemberResponseDto> searchGroupMembers(Long groupId, String targetName,
        String username) {
        List<GroupMemberResponseDto> groupMemberResponseDtoList = groupRepository.findGroupMembersByGroupAndNameLikeExceptUser(
            groupId, targetName, username);
        for (GroupMemberResponseDto dto : groupMemberResponseDtoList) {
            dto.setUserId(MaskingUtil.maskUserId(dto.getUserId()));
        }
        return groupMemberResponseDtoList;
    }
}
