package com.uspray.uspray.domain;

import com.uspray.uspray.Enums.DeleteReason;
import com.uspray.uspray.common.domain.AuditingTimeEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeleteManager extends AuditingTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "delete_manager_id")
    private Long id;

    private DeleteReason deleteReason;
    private String reason;

    private Long memberId;

    @Builder
    public DeleteManager(DeleteReason deleteReason, String reason, Long memberId) {
        this.deleteReason = deleteReason;
        this.reason = reason;
        this.memberId = memberId;
    }



}
