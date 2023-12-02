package com.uspray.uspray.service;

import com.uspray.uspray.DTO.group.request.GroupMemberRequestDto;
import com.uspray.uspray.DTO.group.request.GroupRequestDto;
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


@Service
@RequiredArgsConstructor
public class GroupFacade {

    private final GroupMemberRepository groupMemberRepository;
    private final GroupRepository groupRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void createGroup(String username, GroupRequestDto groupRequestDto) {
        Group group = Group.builder()
            .name(groupRequestDto.getName())
            .leader(memberRepository.getMemberByUserId(username))
            .build();
        groupRepository.save(group);

        addGroupMember(username, group.getId());
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
    public void changeGroupLeader(String username, Long groupId, Long newLeaderId) {
        Member member = memberRepository.getMemberByUserId(username);
        Member newLeader = memberRepository.getMemberById(newLeaderId);
        Group group = groupRepository.getGroupById(groupId);

        group.checkLeaderAuthorization(member);
        group.changeLeader(newLeader);
    }

    @Transactional
    public void kickGroupMember(String username, Long groupId, Long kickedMemberId) {
        Member leader = memberRepository.getMemberByUserId(username);
        Group group = groupRepository.getGroupById(groupId);
        GroupMember kickedgroupMember = groupMemberRepository.getGroupMemberByGroupIdAndMemberId(groupId, kickedMemberId);

        group.checkLeaderAuthorization(leader);
        if (group.getLeader().getId().equals(kickedMemberId)) {
            throw new CustomException(ErrorStatus.LEADER_CANNOT_LEAVE_GROUP_EXCEPTION, ErrorStatus.LEADER_CANNOT_LEAVE_GROUP_EXCEPTION.getMessage());
        }
        group.kickMember(kickedgroupMember);
    }

    @Transactional
    public void addGroupMember(String username, Long groupId) {
        Member member = memberRepository.getMemberByUserId(username);
        Group group = groupRepository.getGroupById(groupId);

        if (groupMemberRepository.existsByGroupAndMember(group, member)) {
            throw new CustomException(ErrorStatus.ALREADY_EXIST_GROUP_MEMBER_EXCEPTION, ErrorStatus.ALREADY_EXIST_GROUP_MEMBER_EXCEPTION.getMessage());
        }
        GroupMember groupMember = GroupMember.builder()
            .group(group)
            .member(member)
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

}
