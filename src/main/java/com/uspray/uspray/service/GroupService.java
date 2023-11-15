package com.uspray.uspray.service;

import com.uspray.uspray.DTO.group.response.GroupListResponseDto;
import com.uspray.uspray.DTO.group.response.GroupResponseDto;
import com.uspray.uspray.domain.Group;
import com.uspray.uspray.domain.Member;
import com.uspray.uspray.infrastructure.GroupRepository;
import com.uspray.uspray.infrastructure.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final MemberRepository memberRepository;

    public GroupListResponseDto getGroupList(String username) {
        Member member = memberRepository.getMemberByUserId(username);
        List<GroupResponseDto> groupList = groupRepository.findGroupListByMember(member);
        return new GroupListResponseDto(groupList);
    }
}
