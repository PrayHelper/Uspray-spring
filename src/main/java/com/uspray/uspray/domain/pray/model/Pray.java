package com.uspray.uspray.domain.pray.model;

import com.uspray.uspray.domain.category.model.Category;
import com.uspray.uspray.domain.group.model.GroupPray;
import com.uspray.uspray.domain.member.model.Member;
import com.uspray.uspray.domain.pray.dto.pray.request.PrayUpdateRequestDto;
import com.uspray.uspray.global.common.model.AuditingTimeEntity;
import com.uspray.uspray.global.enums.PrayType;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE pray SET deleted = true WHERE pray_id = ?")
@Where(clause = "deleted=false")
public class Pray extends AuditingTimeEntity {

    private final Boolean deleted = false;
    @OneToMany(cascade = CascadeType.REMOVE, orphanRemoval = true)
    private final List<GroupPray> groupPray = new ArrayList<>();
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pray_id")
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;
    private String content;
    private Integer count;
    private LocalDate deadline;
    private Boolean isShared = false;
    @Column(name = "origin_pray_id")
    private Long originPrayId;
    @Column(name = "origin_member_id")
    private Long originMemberId;
    @NotNull
    @Enumerated(EnumType.STRING)
    private PrayType prayType;
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @NotNull
    private LocalDate startDate;

    private LocalDate lastPrayedAt;

    @Builder
    public Pray(Member member, String content, LocalDate deadline, Long originPrayId,
        Long originMemberId, Category category, PrayType prayType, GroupPray groupPray,
        LocalDate startDate, Boolean isShared) {
        this.member = member;
        this.content = new String(Base64.getEncoder().encode(content.getBytes()));
        this.count = 0;
        this.deadline = deadline;
        this.originPrayId = originPrayId;
        this.originMemberId = originMemberId;
        this.category = category;
        this.prayType = prayType;
        setGroupPray(groupPray);
        this.startDate = startDate;
        this.lastPrayedAt = LocalDate.of(2002, 2, 24);
        this.isShared = isShared;
    }

    public static Pray createdByGroupPrayOf(Member author, String content, LocalDate deadline,
        Category category, PrayType prayType, GroupPray groupPray, Boolean isShared) {
        return Pray.builder()
            .member(author)
            .content(content)
            .deadline(deadline)
            .category(category)
            .startDate(LocalDate.now())
            .prayType(prayType)
            .groupPray(groupPray)
            .isShared(isShared)
            .build();
    }

    public static Pray createdByScrapOf(Member member, String content, LocalDate deadline,
        Long originMemberId, Long originPrayId, Category category, PrayType prayType) {
        return Pray.builder()
            .member(member)
            .content(content)
            .deadline(deadline)
            .originMemberId(originMemberId)
            .originPrayId(originPrayId)
            .category(category)
            .prayType(prayType)
            .build();
    }

    public void setGroupPray(GroupPray groupPray) {
        if (groupPray != null) {
            this.groupPray.add(groupPray);
            groupPray.setOriginPray(this);
        }
    }

    public Pray update(PrayUpdateRequestDto prayUpdateRequestDto, Category category) {
        if (prayUpdateRequestDto.getContent() != null) {
            this.content = new String(
                Base64.getEncoder().encode(prayUpdateRequestDto.getContent().getBytes()));
        }
        this.deadline = prayUpdateRequestDto.getDeadline();
        this.category = category;
        return this;
    }

    public void countUp() {
        this.count++;
        this.lastPrayedAt = LocalDate.now();
    }

    public void complete() {
        this.deadline = LocalDate.now();
    }

    public void deleteLastPrayedAt() {
        this.lastPrayedAt = LocalDate.now().minusDays(1);
        this.count--;
    }

    public void setIsShared() {
        this.isShared = true;
    }
}
