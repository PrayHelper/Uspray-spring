package com.uspray.uspray.domain;

import com.uspray.uspray.DTO.pray.request.PrayUpdateRequestDto;
import com.uspray.uspray.Enums.PrayType;
import com.uspray.uspray.common.domain.AuditingTimeEntity;
import com.uspray.uspray.exception.ErrorStatus;
import com.uspray.uspray.exception.model.NotFoundException;
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
    @NotNull
    @Enumerated(EnumType.STRING)
    private PrayType prayType;
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    private LocalDate lastPrayedAt;

    @Builder
    public Pray(Member member, String content, LocalDate deadline, Long originPrayId,
        Category category, PrayType prayType, GroupPray groupPray) {
        this.member = member;
        this.content = new String(Base64.getEncoder().encode(content.getBytes()));
        this.count = 0;
        this.deadline = deadline;
        this.originPrayId = originPrayId;
        this.isShared = (originPrayId != null);
        this.category = category;
        this.prayType = prayType;
        setGroupPray(groupPray);
        this.lastPrayedAt = LocalDate.of(2002, 2, 24);
    }

    public void setGroupPray(GroupPray groupPray) {
        if (groupPray != null) {
            this.groupPray.add(groupPray);
            groupPray.setOriginPray(this);
        }
    }

    public void update(PrayUpdateRequestDto prayUpdateRequestDto,
        boolean isShared, Category category) {
        handleUpdateContentSharedPray(isShared, prayUpdateRequestDto.getContent());
        if (prayUpdateRequestDto.getContent() != null) {
            this.content = new String(
                Base64.getEncoder().encode(prayUpdateRequestDto.getContent().getBytes()));
        }
        this.deadline = prayUpdateRequestDto.getDeadline();
        if (category != null) {
            this.category = category;
        }
    }

    private void handleUpdateContentSharedPray(boolean isShared, String content) {
        if (isShared && content != null) {
            throw new NotFoundException(ErrorStatus.ALREADY_SHARED_EXCEPTION,
                ErrorStatus.ALREADY_SHARED_EXCEPTION.getMessage());
        }
    }

    public String getContent() {
        return new String(Base64.getDecoder().decode(content));
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
