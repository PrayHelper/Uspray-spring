package com.uspray.uspray.global.auth.model;

import com.uspray.uspray.global.enums.WithdrawReason;
import com.uspray.uspray.global.common.model.AuditingTimeEntity;
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
public class Withdraw extends AuditingTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "withdraw_id")
    private Long id;

    private WithdrawReason withdrawReason;
    private String description;

    private Long memberId;

    @Builder
    public Withdraw(WithdrawReason withdrawReason, String description, Long memberId) {
        this.withdrawReason = withdrawReason;
        this.description = description;
        this.memberId = memberId;
    }



}
