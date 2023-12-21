package com.uspray.uspray.domain;

import com.uspray.uspray.common.domain.AuditingTimeEntity;
import com.uspray.uspray.exception.ErrorStatus;
import com.uspray.uspray.exception.model.CustomException;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
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

    @OneToMany(mappedBy = "group", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private final List<GroupMember> groupMemberList = new ArrayList<>();

    @OneToMany(mappedBy = "group", orphanRemoval = true)
    private final List<GroupPray> groupPrayList = new ArrayList<>();

    @Builder
    public Group(String name, Member leader) {
        this.name = name;
        this.leader = leader;
    }

    public void changeName(String name) {
        this.name = name;
    }

    public void changeLeader(Member member) {
        this.leader = member;
    }

    public void kickMember(GroupMember groupMember) {
        this.groupMemberList.remove(groupMember);

    }

    public void validateGroupName(String newName) {
        if (this.name.equals(newName)) {
            throw new CustomException(ErrorStatus.ALREADY_EXIST_GROUP_NAME_EXCEPTION,
                ErrorStatus.ALREADY_EXIST_GROUP_NAME_EXCEPTION.getMessage());
        }
    }

    public void checkLeaderAuthorization(Member member) {
        Member leader = this.getLeader();
        if (!leader.equals(member)) {
            throw new CustomException(ErrorStatus.GROUP_UNAUTHORIZED_EXCEPTION,
                ErrorStatus.GROUP_UNAUTHORIZED_EXCEPTION.getMessage());
        }
    }
}
