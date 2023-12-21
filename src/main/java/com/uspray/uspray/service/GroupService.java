package com.uspray.uspray.service;

import com.uspray.uspray.DTO.group.response.GroupListResponseDto;
import com.uspray.uspray.DTO.group.response.GroupMemberResponseDto;
import com.uspray.uspray.DTO.group.response.GroupResponseDto;
import com.uspray.uspray.infrastructure.GroupRepository;
import com.uspray.uspray.util.MaskingUtil;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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
        List<GroupResponseDto> filterMostRecentUpdatedAt =
            groupList.stream()
                .collect(Collectors.groupingBy(GroupResponseDto::getId))
                .entrySet().stream()
                .map(Map.Entry::getValue)
                .map(groupResponseDtos -> groupResponseDtos.stream()
                    .max((o1, o2) -> o1.getUpdatedAt().compareTo(o2.getUpdatedAt()))
                    .orElseThrow(() -> new IllegalArgumentException("그룹이 존재하지 않습니다.")))
                .collect(Collectors.toList());

        return new GroupListResponseDto(filterMostRecentUpdatedAt);
    }

    @Transactional(readOnly = true)
    public List<GroupMemberResponseDto> searchGroupMembers(Long groupId, String name) {
        List<GroupMemberResponseDto> groupMemberResponseDtoList = groupRepository.findGroupMembersByGroupAndNameLike(
            groupId, name);
        for (GroupMemberResponseDto dto : groupMemberResponseDtoList) {
            dto.setUserId(MaskingUtil.maskUserId(dto.getUserId()));
        }
        return groupMemberResponseDtoList;
    }
}
