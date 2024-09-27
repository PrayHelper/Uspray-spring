package com.uspray.uspray.domain.group.repository;

import com.uspray.uspray.domain.group.model.Group;
import com.uspray.uspray.domain.group.model.GroupMember;
import com.uspray.uspray.domain.member.model.Member;
import com.uspray.uspray.global.exception.ErrorStatus;
import com.uspray.uspray.global.exception.model.NotFoundException;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {


    boolean existsByGroupAndMember(Group group, Member member);

    Optional<GroupMember> findByGroupAndMember(Group group, Member member);

    Optional<GroupMember> findByGroupIdAndMemberId(Long groupId, Long memberId);

    List<GroupMember> findByGroupId(Long groupId);

    default GroupMember getGroupMemberByGroupAndMember(Group group, Member member) {
        return this.findByGroupAndMember(group, member).orElseThrow(
            () -> new NotFoundException(ErrorStatus.NOT_FOUND_GROUP_MEMBER_EXCEPTION));
    }

    default GroupMember getGroupMemberByGroupIdAndMemberId(Long groupId, Long memberId) {
        return this.findByGroupIdAndMemberId(groupId, memberId).orElseThrow(
            () -> new NotFoundException(ErrorStatus.NOT_FOUND_GROUP_MEMBER_EXCEPTION));
    }

    GroupMember findGroupMemberByMemberAndGroupId(Member member, Long groupId);
}
