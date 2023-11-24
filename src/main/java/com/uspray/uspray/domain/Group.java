package com.uspray.uspray.domain;

import com.uspray.uspray.common.domain.AuditingTimeEntity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.*;

import com.uspray.uspray.exception.ErrorStatus;
import com.uspray.uspray.exception.model.CustomException;
import com.uspray.uspray.exception.model.NotFoundException;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "club")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Group extends AuditingTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_id")
    private Long id;

    private String name;

    @OneToOne
    @JoinColumn(name = "leader_id", referencedColumnName = "member_id")
    private Member leader;

    @ManyToMany(mappedBy = "groups")
    private Set<Member> members = new HashSet<>();

    @OneToMany(mappedBy = "group", orphanRemoval = true)
    private List<GroupPray> groupPrayList;

    @Builder
    public Group(String name, Member leader) {
        this.name = name;
        this.members.add(leader);
        this.leader = leader;
    }

    public void changeName(String name) {
        this.name = name;
    }

    public void changeLeader(Member leader) {
        this.leader = leader;
    }

    public void addMember(Member member) {
        this.members.add(member);
    }

    public void kickMember(Member member) {
        this.members.remove(member);
    }

    public void validateGroupName(String newName) {
        if (this.name.equals(newName)) {
            throw new CustomException(ErrorStatus.ALREADY_EXIST_GROUP_NAME_EXCEPTION, ErrorStatus.ALREADY_EXIST_GROUP_NAME_EXCEPTION.getMessage());
        }
    }

    public void checkLeaderAuthorization(Member member) {
        if (!this.leader.equals(member)) {
            throw new CustomException(ErrorStatus.GROUP_UNAUTHORIZED_EXCEPTION, ErrorStatus.GROUP_UNAUTHORIZED_EXCEPTION.getMessage());
        }
    }

    public void checkGroupMember(Member member) {
        if (!this.members.contains(member)) {
            throw new NotFoundException(ErrorStatus.GROUP_MEMBER_NOT_FOUND_EXCEPTION, ErrorStatus.GROUP_MEMBER_NOT_FOUND_EXCEPTION.getMessage());
        }
    }
}
