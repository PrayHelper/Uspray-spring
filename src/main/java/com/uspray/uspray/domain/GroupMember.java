package com.uspray.uspray.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class GroupMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "groupmember_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    public GroupMember(Group group, Member member) {
        setGroup(group);
        setMember(member);
    }

    private void setMember(Member member) {
        this.member = member;
        member.getGroupMemberList().add(this);
    }

    private void setGroup(Group group) {
        this.group = group;
        group.getGroupMemberList().add(this);
    }
}
