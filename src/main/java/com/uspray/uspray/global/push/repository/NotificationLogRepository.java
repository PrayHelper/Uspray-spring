package com.uspray.uspray.global.push.repository;

import com.uspray.uspray.global.push.model.NotificationLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationLogRepository extends
    JpaRepository<NotificationLog, Long> {

}
