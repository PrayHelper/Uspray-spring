package com.uspray.uspray.service;

import com.uspray.uspray.DTO.group.request.GroupRequestDto;
import com.uspray.uspray.DTO.group.response.GroupDetailResponseDto;
import com.uspray.uspray.DTO.group.response.GroupListResponseDto;
import com.uspray.uspray.DTO.group.response.GroupResponseDto;
import com.uspray.uspray.domain.Group;
import com.uspray.uspray.domain.Member;
import com.uspray.uspray.infrastructure.GroupRepository;
import com.uspray.uspray.infrastructure.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public GroupListResponseDto getGroupList(String username) {
        Member member = memberRepository.getMemberByUserId(username);
        List<GroupResponseDto> groupList = groupRepository.findGroupListByMember(member);
        return new GroupListResponseDto(groupList);
    }

    @Transactional
    public void createGroup(String username, GroupRequestDto groupRequestDto) {
        Member member = memberRepository.getMemberByUserId(username);
        Group group = Group.builder()
                .name(groupRequestDto.getName())
                .leader(member)
                .build();
        member.getGroups().add(group);
        groupRepository.save(group);
    }
}
