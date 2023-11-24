package com.uspray.uspray.domain;

import com.uspray.uspray.common.domain.AuditingTimeEntity;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class NotificationLog extends AuditingTimeEntity {

    @Id
    private Long id;
    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;
    private String title;

}
