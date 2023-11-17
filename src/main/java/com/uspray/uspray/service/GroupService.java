package com.uspray.uspray.service;

import com.uspray.uspray.DTO.group.request.GroupMemberRequestDto;
import com.uspray.uspray.DTO.group.request.GroupRequestDto;
import com.uspray.uspray.DTO.group.response.GroupListResponseDto;
import com.uspray.uspray.DTO.group.response.GroupResponseDto;
import com.uspray.uspray.domain.Group;
import com.uspray.uspray.domain.Member;
import com.uspray.uspray.exception.ErrorStatus;
import com.uspray.uspray.exception.model.CustomException;
import com.uspray.uspray.exception.model.NotFoundException;
import com.uspray.uspray.infrastructure.GroupPrayRepository;
import com.uspray.uspray.infrastructure.GroupRepository;
import com.uspray.uspray.infrastructure.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final MemberRepository memberRepository;
    private final GroupPrayRepository groupPrayRepository;

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

    @Transactional
    public void changeGroupName(String username, Long groupId, GroupRequestDto groupRequestDto) {
        Member member = memberRepository.getMemberByUserId(username);
        Group group = groupRepository.getGroupById(groupId);

        if (!group.getLeader().equals(member)) {
            throw new CustomException(ErrorStatus.GROUP_UNAUTHORIZED_EXCEPTION, ErrorStatus.GROUP_UNAUTHORIZED_EXCEPTION.getMessage());
        }
        if (group.getName().equals(groupRequestDto.getName()) || groupRepository.existsByName(groupRequestDto.getName())) {
            throw new CustomException(ErrorStatus.ALREADY_EXIST_GROUP_NAME_EXCEPTION, ErrorStatus.ALREADY_EXIST_GROUP_NAME_EXCEPTION.getMessage());
        }
        group.changeName(groupRequestDto.getName());
    }

    @Transactional
    public void changeGroupLeader(String username, Long groupId, GroupMemberRequestDto groupLeaderRequestDto) {
        Member member = memberRepository.getMemberByUserId(username);
        Group group = groupRepository.getGroupById(groupId);

        if (!group.getLeader().equals(member)) {
            throw new CustomException(ErrorStatus.GROUP_UNAUTHORIZED_EXCEPTION, ErrorStatus.GROUP_UNAUTHORIZED_EXCEPTION.getMessage());
        }
        if (!group.getMembers().contains(memberRepository.getMemberByUserId(groupLeaderRequestDto.getUsername()))) {
            throw new NotFoundException(ErrorStatus.GROUP_MEMBER_NOT_FOUND_EXCEPTION, ErrorStatus.GROUP_MEMBER_NOT_FOUND_EXCEPTION.getMessage());
        }
        group.changeLeader(memberRepository.getMemberByUserId(groupLeaderRequestDto.getUsername()));
    }

    @Transactional
    public void kickGroupMember(String username, Long groupId, GroupMemberRequestDto groupKickRequestDto) {
        Member leader = memberRepository.getMemberByUserId(username);
        Group group = groupRepository.getGroupById(groupId);
        Member kickedMember = memberRepository.getMemberByUserId(groupKickRequestDto.getUsername());

        if (!group.getLeader().equals(leader)) {
            throw new CustomException(ErrorStatus.GROUP_UNAUTHORIZED_EXCEPTION, ErrorStatus.GROUP_UNAUTHORIZED_EXCEPTION.getMessage());
        }
        if (!group.getMembers().contains(kickedMember)) {
            throw new NotFoundException(ErrorStatus.GROUP_MEMBER_NOT_FOUND_EXCEPTION, ErrorStatus.GROUP_MEMBER_NOT_FOUND_EXCEPTION.getMessage());
        }
        kickedMember.leaveGroup(group);
        group.kickMember(kickedMember);
    }

    // 일단은 관리자만 회원을 추가할 수 있게끔 설정해두었습니다
    // 추후 수정될 수 있음 -> 초대 링크 생성하고 링크 방문시 수락으로 변경 예정
    @Transactional
    public void addGroupMember(String username, Long groupId, GroupMemberRequestDto groupAddRequestDto) {
        Member member = memberRepository.getMemberByUserId(username);
        Group group = groupRepository.getGroupById(groupId);
        Member addedMember = memberRepository.getMemberByUserId(groupAddRequestDto.getUsername());

        if (!group.getMembers().contains(member)) {
            throw new CustomException(ErrorStatus.GROUP_MEMBER_NOT_FOUND_EXCEPTION, ErrorStatus.GROUP_MEMBER_NOT_FOUND_EXCEPTION.getMessage());
        }
        if (group.getMembers().contains(addedMember)) {
            throw new CustomException(ErrorStatus.ALREADY_EXIST_GROUP_MEMBER_EXCEPTION, ErrorStatus.ALREADY_EXIST_GROUP_MEMBER_EXCEPTION.getMessage());
        }
        addedMember.joinGroup(group);
        group.addMember(addedMember);
    }

    @Transactional
    public void leaveGroup(String username, Long groupId) {
        Member member = memberRepository.getMemberByUserId(username);
        Group group = groupRepository.getGroupById(groupId);

        if (group.getLeader().equals(member)) {
            throw new CustomException(ErrorStatus.LEADER_CANNOT_LEAVE_GROUP_EXCEPTION, ErrorStatus.LEADER_CANNOT_LEAVE_GROUP_EXCEPTION.getMessage());
        }
        member.leaveGroup(group);
        group.kickMember(member);
    }

    @Transactional
    public void deleteGroup(String username, Long groupId) {
        Member leader = memberRepository.getMemberByUserId(username);
        Group group = groupRepository.getGroupById(groupId);

        if (!group.getLeader().equals(leader)) {
            throw new CustomException(ErrorStatus.GROUP_UNAUTHORIZED_EXCEPTION, ErrorStatus.GROUP_UNAUTHORIZED_EXCEPTION.getMessage());
        }
        Set<Member> members = group.getMembers();
        for (Member member : members) {
            member.leaveGroup(group);
            group.kickMember(member);
        }
        groupRepository.delete(group);
        groupPrayRepository.deleteAllByGroup(group);
    }
}
