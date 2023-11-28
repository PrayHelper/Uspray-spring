package com.uspray.uspray.domain;

import com.uspray.uspray.common.domain.AuditingTimeEntity;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;

import com.uspray.uspray.exception.ErrorStatus;
import com.uspray.uspray.exception.model.CustomException;
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

    @OneToMany(mappedBy = "group", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<GroupMember> groupMemberList = new ArrayList<>();

    @OneToMany(mappedBy = "group", orphanRemoval = true)
    private List<GroupPray> groupPrayList;

    @Builder
    public Group(String name) {
        this.name = name;
    }

    public void changeName(String name) {
        this.name = name;
    }

    public Member getLeader() {
        for (GroupMember gm : this.groupMemberList) {
            if (gm.isLeader()) {
                return gm.getMember();
            }
        }
        return null;
    }

    public void changeLeader(Member leader) {
        for (GroupMember gm : this.groupMemberList) {
            gm.setLeader(gm.getMember().equals(leader));
        }
    }

    public void kickMember(GroupMember groupMember) {
        this.groupMemberList.remove(groupMember);
    }

    public void validateGroupName(String newName) {
        if (this.name.equals(newName)) {
            throw new CustomException(ErrorStatus.ALREADY_EXIST_GROUP_NAME_EXCEPTION, ErrorStatus.ALREADY_EXIST_GROUP_NAME_EXCEPTION.getMessage());
        }
    }

    public void checkLeaderAuthorization(Member member) {
        Member leader = this.getLeader();
        if (!leader.equals(member)) {
            throw new CustomException(ErrorStatus.GROUP_UNAUTHORIZED_EXCEPTION, ErrorStatus.GROUP_UNAUTHORIZED_EXCEPTION.getMessage());
        }
    }
}
