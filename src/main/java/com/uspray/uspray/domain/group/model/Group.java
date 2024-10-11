package com.uspray.uspray.domain.group.model;

import com.uspray.uspray.global.common.model.AuditingTimeEntity;
import com.uspray.uspray.domain.member.model.Member;
import com.uspray.uspray.global.exception.ErrorStatus;
import com.uspray.uspray.global.exception.model.CustomException;
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
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Getter
@Table(name = "club")
@SQLDelete(sql = "UPDATE club SET deleted = true WHERE group_id = ?")
@Where(clause = "deleted=false")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Group extends AuditingTimeEntity {

    @OneToMany(mappedBy = "group", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private final List<GroupMember> groupMemberList = new ArrayList<>();
    @OneToMany(mappedBy = "group", orphanRemoval = true)
    private final List<GroupPray> groupPrayList = new ArrayList<>();
    private final Boolean deleted = false;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_id")
    private Long id;
    private String name;
    @OneToOne
    @JoinColumn(name = "leader_id", referencedColumnName = "member_id")
    private Member leader;

    public static Group of(String name, Member leader) {
        return Group.builder()
            .name(name)
            .leader(leader)
            .build();
    }

    @Builder
    public Group(String name, Member leader) {
        this.name = name;
        this.leader = leader;
    }

    public void changeName(String name) {
        this.name = name;
    }

    public void changeLeader(Member leader, Member newLeader) {
        checkLeaderAuthorization(leader);
        this.leader = newLeader;
    }

    public void kickMember(GroupMember groupMember) {
        this.groupMemberList.remove(groupMember);

    }

    public void checkLeaderAuthorization(Member member) {
        if (!member.equals(this.leader)) {
            throw new CustomException(ErrorStatus.GROUP_UNAUTHORIZED_EXCEPTION);
        }
    }
}
