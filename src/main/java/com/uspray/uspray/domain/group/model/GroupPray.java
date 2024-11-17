package com.uspray.uspray.domain.group.model;

import com.uspray.uspray.global.common.model.AuditingTimeEntity;
import com.uspray.uspray.domain.member.model.Member;
import com.uspray.uspray.domain.pray.model.Pray;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
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
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE group_pray SET deleted = true WHERE grouppray_id = ?")
@Where(clause = "deleted=false")
public class GroupPray extends AuditingTimeEntity {

    @OneToMany(mappedBy = "groupPray", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private final List<ScrapAndHeart> scrapAndHeart = new ArrayList<>();
    private final Boolean deleted = false;
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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pray_id")
    private Pray originPray;

    @Builder
    public GroupPray(String content, Group group, Member author, LocalDate deadline) {
        this.content = new String(Base64.getEncoder().encode(content.getBytes()));
        setGroup(group);
        setAuthor(author);
        this.deadline = deadline;
    }

    public static GroupPray of(Group group, Member author, LocalDate deadline, String content) {
        return GroupPray.builder()
            .group(group)
            .author(author)
            .deadline(deadline)
            .content(content)
            .build();
    }

    public String getContent() {
        return new String(Base64.getDecoder().decode(content));
    }

    public void setOriginPray(Pray pray) {
        this.originPray = pray;
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
