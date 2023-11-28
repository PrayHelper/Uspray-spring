package com.uspray.uspray.domain;

import com.uspray.uspray.common.domain.AuditingTimeEntity;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class NotificationLog extends AuditingTimeEntity {

    @EmbeddedId
    private NotificationLogId id;

    @Column
    private String title;

    @Builder
    public NotificationLog(NotificationLogId id, String title) {
        this.id = id;
        this.title = title;
    }
}

