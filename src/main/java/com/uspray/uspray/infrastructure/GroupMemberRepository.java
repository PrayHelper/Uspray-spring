package com.uspray.uspray.infrastructure;

import com.uspray.uspray.domain.Group;
import com.uspray.uspray.domain.GroupMember;
import com.uspray.uspray.domain.Member;
import com.uspray.uspray.exception.ErrorStatus;
import com.uspray.uspray.exception.model.NotFoundException;
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
            () -> new NotFoundException(ErrorStatus.NOT_FOUND_GROUP_MEMBER_EXCEPTION,
                ErrorStatus.NOT_FOUND_GROUP_MEMBER_EXCEPTION.getMessage()));
    }

    default GroupMember getGroupMemberByGroupIdAndMemberId(Long groupId, Long memberId) {
        return this.findByGroupIdAndMemberId(groupId, memberId).orElseThrow(
            () -> new NotFoundException(ErrorStatus.NOT_FOUND_GROUP_MEMBER_EXCEPTION,
                ErrorStatus.NOT_FOUND_GROUP_MEMBER_EXCEPTION.getMessage()));
    }

    GroupMember findGroupMemberByMemberAndGroupId(Member member, Long groupId);
}
