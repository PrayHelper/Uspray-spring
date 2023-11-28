package com.uspray.uspray.service;

import com.uspray.uspray.DTO.auth.response.MemberResponseDto;
import com.uspray.uspray.DTO.group.request.GroupMemberRequestDto;
import com.uspray.uspray.DTO.group.request.GroupRequestDto;
import com.uspray.uspray.DTO.group.response.GroupListResponseDto;
import com.uspray.uspray.DTO.group.response.GroupResponseDto;
import com.uspray.uspray.domain.Group;
import com.uspray.uspray.domain.GroupMember;
import com.uspray.uspray.domain.Member;
import com.uspray.uspray.exception.ErrorStatus;
import com.uspray.uspray.exception.model.CustomException;
import com.uspray.uspray.infrastructure.GroupMemberRepository;
import com.uspray.uspray.infrastructure.GroupRepository;
import com.uspray.uspray.infrastructure.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupFacade {

    private final GroupMemberRepository groupMemberRepository;
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
            .build();
        groupRepository.save(group);

        addGroupMember(username, group.getId(), true);
    }

    @Transactional
    public void changeGroupName(String username, Long groupId, GroupRequestDto groupRequestDto) {
        Member member = memberRepository.getMemberByUserId(username);
        Group group = groupRepository.getGroupById(groupId);

        group.validateGroupName(groupRequestDto.getName());
        group.checkLeaderAuthorization(member);
        group.changeName(groupRequestDto.getName());
    }

    @Transactional
    public void changeGroupLeader(String username, Long groupId, GroupMemberRequestDto groupLeaderRequestDto) {
        Member member = memberRepository.getMemberByUserId(username);
        Member newLeader = memberRepository.getMemberByUserId(groupLeaderRequestDto.getUsername());
        Group group = groupRepository.getGroupById(groupId);

        group.checkLeaderAuthorization(member);
        group.changeLeader(newLeader);
    }

    @Transactional
    public void kickGroupMember(String username, Long groupId, GroupMemberRequestDto groupKickRequestDto) {
        Member leader = memberRepository.getMemberByUserId(username);
        Group group = groupRepository.getGroupById(groupId);
        Member kickedMember = memberRepository.getMemberByUserId(groupKickRequestDto.getUsername());
        GroupMember kickedgroupMember = groupMemberRepository.getGroupMemberByGroupAndMember(group, kickedMember);

        group.checkLeaderAuthorization(leader);
        if (group.getLeader().equals(kickedMember)) {
            throw new CustomException(ErrorStatus.LEADER_CANNOT_LEAVE_GROUP_EXCEPTION, ErrorStatus.LEADER_CANNOT_LEAVE_GROUP_EXCEPTION.getMessage());
        }
        group.kickMember(kickedgroupMember);
    }

    @Transactional
    public void addGroupMember(String username, Long groupId, Boolean isLeader) {
        Member member = memberRepository.getMemberByUserId(username);
        Group group = groupRepository.getGroupById(groupId);

        if (groupMemberRepository.existsByGroupAndMember(group, member)) {
            throw new CustomException(ErrorStatus.ALREADY_EXIST_GROUP_MEMBER_EXCEPTION, ErrorStatus.ALREADY_EXIST_GROUP_MEMBER_EXCEPTION.getMessage());
        }
        GroupMember groupMember = GroupMember.builder()
            .group(group)
            .member(member)
            .isLeader(isLeader)
            .build();
        groupMemberRepository.save(groupMember);
    }

    @Transactional
    public void leaveGroup(String username, Long groupId) {
        Member member = memberRepository.getMemberByUserId(username);
        Group group = groupRepository.getGroupById(groupId);
        GroupMember groupMember = groupMemberRepository.getGroupMemberByGroupAndMember(group, member);

        if (group.getLeader().equals(member)) {
            throw new CustomException(ErrorStatus.LEADER_CANNOT_LEAVE_GROUP_EXCEPTION, ErrorStatus.LEADER_CANNOT_LEAVE_GROUP_EXCEPTION.getMessage());
        }
        group.kickMember(groupMember);
    }

    @Transactional
    public void deleteGroup(String username, Long groupId) {
        Member leader = memberRepository.getMemberByUserId(username);
        Group group = groupRepository.getGroupById(groupId);

        group.checkLeaderAuthorization(leader);
        groupRepository.delete(group);
    }

    @Transactional(readOnly = true)
    public List<MemberResponseDto> searchGroupMembers(String username, Long groupId, String name) {
        if (name == null) {
            return groupRepository.findGroupMembers(groupId);
        }
        return groupRepository.findGroupMembersByGroupAndNameLike(groupId, name);
    }
}
