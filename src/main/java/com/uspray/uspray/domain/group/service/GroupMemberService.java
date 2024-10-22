package com.uspray.uspray.domain.group.service;

import com.uspray.uspray.domain.group.model.Group;
import com.uspray.uspray.domain.group.model.GroupMember;
import com.uspray.uspray.domain.group.repository.GroupMemberRepository;
import com.uspray.uspray.domain.member.model.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GroupMemberService {

    private final GroupMemberRepository groupMemberRepository;

    @Transactional
    public GroupMember createGroupMember(GroupMember groupMember) {
        return groupMemberRepository.save(groupMember);
    }

    public GroupMember getGroupMemberByGroupIdAndMemberId(Long groupId, Long memberId) {
        return groupMemberRepository.getGroupMemberByGroupIdAndMemberId(groupId, memberId);
    }

    public Boolean existsByGroupAndMember(Group group, Member member) {
        return groupMemberRepository.existsByGroupAndMember(group, member);
    }

    @Transactional
    public void delete(GroupMember groupMember) {
        groupMemberRepository.delete(groupMember);
    }

    @Transactional
    public void deleteAllByGroup(Group group) {
        groupMemberRepository.deleteAllByGroup(group);
    }

    public GroupMember getGroupMemberByGroupAndMember(Group group, Member member) {
        return groupMemberRepository.getGroupMemberByGroupAndMember(group, member);
    }

}
