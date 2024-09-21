package com.uspray.uspray.domain.group.service;

import com.uspray.uspray.domain.group.dto.group.request.GroupRequestDto;
import com.uspray.uspray.domain.group.model.Group;
import com.uspray.uspray.domain.group.model.GroupMember;
import com.uspray.uspray.domain.member.model.Member;
import com.uspray.uspray.global.exception.ErrorStatus;
import com.uspray.uspray.global.exception.model.CustomException;
import com.uspray.uspray.domain.group.repository.GroupMemberRepository;
import com.uspray.uspray.domain.group.repository.GroupPrayRepository;
import com.uspray.uspray.domain.group.repository.GroupRepository;
import com.uspray.uspray.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class GroupFacade {

    private final GroupMemberRepository groupMemberRepository;
    private final GroupRepository groupRepository;
    private final MemberRepository memberRepository;
    private final GroupPrayRepository groupPrayRepository;

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
        GroupMember kickedgroupMember = groupMemberRepository.getGroupMemberByGroupIdAndMemberId(
            groupId, kickedMemberId);

        group.checkLeaderAuthorization(leader);
        if (group.getLeader().getId().equals(kickedMemberId)) {
            throw new CustomException(ErrorStatus.LEADER_CANNOT_LEAVE_GROUP_EXCEPTION);
        }
        group.kickMember(kickedgroupMember);
        groupMemberRepository.delete(kickedgroupMember);
    }

    @Transactional
    public void addGroupMember(String username, Long groupId) {
        Member member = memberRepository.getMemberByUserId(username);
        Group group = groupRepository.getGroupById(groupId);

        if (groupMemberRepository.existsByGroupAndMember(group, member)) {
            throw new CustomException(ErrorStatus.ALREADY_EXIST_GROUP_MEMBER_EXCEPTION);
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
        GroupMember groupMember = groupMemberRepository.getGroupMemberByGroupAndMember(group,
            member);

        if (group.getLeader().equals(member)) {
            throw new CustomException(ErrorStatus.LEADER_CANNOT_LEAVE_GROUP_EXCEPTION);
        }
        group.kickMember(groupMember);
        groupMemberRepository.delete(groupMember);
    }

    @Transactional
    public void deleteGroup(String username, Long groupId) {
        Member leader = memberRepository.getMemberByUserId(username);
        Group group = groupRepository.getGroupById(groupId);

        group.checkLeaderAuthorization(leader);
        groupPrayRepository.deleteAllByGroup(group);
        groupRepository.delete(group);
    }

    @Transactional
    public void changeGroupNotification(String username, Long groupId) {
        Member member = memberRepository.getMemberByUserId(username);
        GroupMember groupMember = groupMemberRepository.findGroupMemberByMemberAndGroupId(
            member,
            groupId);
        System.out.println(groupMember.getNotificationAgree());
        groupMember.setNotificationAgree(!groupMember.getNotificationAgree());
        System.out.println(groupMember.getNotificationAgree());
    }

}
