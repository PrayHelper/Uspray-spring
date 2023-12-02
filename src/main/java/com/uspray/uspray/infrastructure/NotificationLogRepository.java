package com.uspray.uspray.infrastructure;

import com.uspray.uspray.domain.NotificationLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationLogRepository extends
    JpaRepository<NotificationLog, Long> {

}
