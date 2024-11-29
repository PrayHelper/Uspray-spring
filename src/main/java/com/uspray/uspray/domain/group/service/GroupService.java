package com.uspray.uspray.domain.group.service;

import com.uspray.uspray.domain.group.dto.group.response.GroupListResponseDto;
import com.uspray.uspray.domain.group.dto.group.response.GroupMemberResponseDto;
import com.uspray.uspray.domain.group.dto.group.response.GroupResponseDto;
import com.uspray.uspray.domain.group.model.Group;
import com.uspray.uspray.domain.group.repository.GroupRepository;
import com.uspray.uspray.global.exception.ErrorStatus;
import com.uspray.uspray.global.exception.model.CustomException;
import com.uspray.uspray.global.util.MaskingUtil;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;

    @Transactional
    public Group create(Group group) {
        return groupRepository.save(group);
    }

    public Group getGroupById(Long groupId) {
        return groupRepository.getGroupById(groupId);
    }

    public Group getGroupByIdAndLeaderId(Long groupId, Long leaderId) {
        return groupRepository.findByIdAndLeaderId(groupId, leaderId).orElseThrow(() -> new CustomException(
            ErrorStatus.GROUP_UNAUTHORIZED_EXCEPTION));
    }

    @Transactional
    public void delete(Group group) {
        groupRepository.delete(group);
    }

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
