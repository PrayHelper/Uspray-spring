package com.uspray.uspray.domain;

import com.uspray.uspray.common.domain.AuditingTimeEntity;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupPray extends AuditingTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "grouppray_id")
    private Long id;

    private String content;

    private LocalDate deadline;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member author;

    @OneToMany(mappedBy = "groupPray", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private final List<ScrapAndHeart> scrapAndHeart = new ArrayList<>();

    @Builder
    public GroupPray(String content, Group group, Member author, LocalDate deadline) {
        this.content = content;
        setGroup(group);
        setAuthor(author);
        this.deadline = deadline;
    }

    private void setGroup(Group group) {
        this.group = group;
        group.getGroupPrayList().add(this);
    }

    private void setAuthor(Member author) {
        this.author = author;
        author.getGroupPrayList().add(this);
    }
}
