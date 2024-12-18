package com.uspray.uspray.domain.group.service;

import com.uspray.uspray.domain.group.dto.group.request.GroupRequestDto;
import com.uspray.uspray.domain.group.model.Group;
import com.uspray.uspray.domain.group.model.GroupMember;
import com.uspray.uspray.domain.member.model.Member;
import com.uspray.uspray.domain.member.service.MemberService;
import com.uspray.uspray.global.exception.ErrorStatus;
import com.uspray.uspray.global.exception.model.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class GroupFacade {

    private final GroupService groupService;
    private final MemberService memberService;
    private final GroupMemberService groupMemberService;
    private final GroupPrayService groupPrayService;

    @Transactional
    public void createGroup(String username, GroupRequestDto groupRequestDto) {

        Member leader = memberService.findMemberByUserId(username);
        Group group = groupService.create(
            Group.of(groupRequestDto.getName(), leader));

        groupMemberService.createGroupMember(GroupMember.of(group, leader));
    }

    @Transactional
    public void changeGroupName(String username, Long groupId, GroupRequestDto groupRequestDto) {
        Member leader = memberService.findMemberByUserId(username);
        Group group = groupService.getGroupByIdAndLeaderId(groupId, leader.getId());

        group.changeName(groupRequestDto.getName());
    }

    @Transactional
    public void changeGroupLeader(String username, Long groupId, Long newLeaderId) {
        Member leader = memberService.findMemberByUserId(username);
        Group group = groupService.getGroupByIdAndLeaderId(groupId, leader.getId());

        group.changeLeader(memberService.findMemberById(newLeaderId));
    }

    @Transactional
    public void kickGroupMember(String username, Long groupId, Long kickedMemberId) {
        Member leader = memberService.findMemberByUserId(username);
        if (leader.getId().equals(kickedMemberId)) {
            throw new CustomException(ErrorStatus.LEADER_CANNOT_LEAVE_GROUP_EXCEPTION);
        }

        Group group = groupService.getGroupByIdAndLeaderId(groupId, leader.getId());
        GroupMember kickedgroupMember = groupMemberService.getGroupMemberByGroupIdAndMemberId(
            group.getId(), kickedMemberId);

        groupMemberService.delete(kickedgroupMember);
    }

    @Transactional
    public void addGroupMember(String username, Long groupId) {
        Member member = memberService.findMemberByUserId(username);
        Group group = groupService.getGroupById(groupId);

        if (groupMemberService.existsByGroupAndMember(group, member)) {
            throw new CustomException(ErrorStatus.ALREADY_EXIST_GROUP_MEMBER_EXCEPTION);
        }

        groupMemberService.createGroupMember(GroupMember.of(group, member));
    }

    @Transactional
    public void leaveGroup(String username, Long groupId) {
        Member member = memberService.findMemberByUserId(username);
        Group group = groupService.getGroupById(groupId);

        if (group.getLeader().equals(member)) {
            throw new CustomException(ErrorStatus.LEADER_CANNOT_LEAVE_GROUP_EXCEPTION);
        }

        GroupMember groupMember = groupMemberService.getGroupMemberByGroupIdAndMemberId(group.getId(),
            member.getId());
        groupMemberService.delete(groupMember);
    }

    @Transactional
    public void deleteGroup(String username, Long groupId) {
        Member leader = memberService.findMemberByUserId(username);
        Group group = groupService.getGroupByIdAndLeaderId(groupId, leader.getId());

        groupPrayService.deleteAllByGroup(group);
        groupMemberService.deleteAllByGroup(group);
        groupService.delete(group);
    }

    @Transactional
    public void changeGroupNotification(String username, Long groupId) {
        Member member = memberService.findMemberByUserId(username);
        GroupMember groupMember = groupMemberService.getGroupMemberByGroupIdAndMemberId(
            member.getId(),
            groupId);
        groupMember.setNotificationAgree(!groupMember.getNotificationAgree());
    }

}
