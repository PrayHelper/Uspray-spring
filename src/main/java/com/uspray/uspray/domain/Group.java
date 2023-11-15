package com.uspray.uspray.domain;

import com.uspray.uspray.common.domain.AuditingTimeEntity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.*;

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

    private String description;

    @OneToOne
    @JoinColumn(name = "leader_id", referencedColumnName = "member_id")
    private Member leader;

    @ManyToMany(mappedBy = "groups")
    private Set<Member> members = new HashSet<>();

    @OneToMany(mappedBy = "group", orphanRemoval = true)
    private List<GroupPray> groupPrayList;

    @Builder
    public Group(String name, String description, Member leader) {
        this.name = name;
        this.description = description;
        this.members.add(leader);
        this.leader = leader;
    }

    private void changeName(String name) {
        this.name = name;
    }

    private void changeDescription(String description) {
        this.description = description;
    }

    private void changeLeader(Member leader) {
        this.leader = leader;
    }

}
