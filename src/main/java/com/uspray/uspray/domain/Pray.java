package com.uspray.uspray.domain;

import com.uspray.uspray.common.domain.AuditingTimeEntity;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Where(clause = "deleted=false")
public class Pray extends AuditingTimeEntity {

    @Id
    @GeneratedValue
    private Long prayId;
    private Long memberId;

    private String content;
    private Integer count;

    private LocalDate deadline;

    private final Boolean isDeleted = false;

    @Builder
    public Pray(Long memberId, String content, Integer count, LocalDate deadline) {
        this.memberId = memberId;
        this.content = content;
        this.count = count;
        this.deadline = deadline;
    }
}
