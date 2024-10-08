package com.uspray.uspray.global.push.service;

import com.uspray.uspray.global.push.model.NotificationLog;
import com.uspray.uspray.global.push.repository.NotificationLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationLogService {
	private final NotificationLogRepository notificationLogRepository;
	@Transactional
	public void saveNotificationLog(NotificationLog notificationLog) {
		notificationLogRepository.save(notificationLog);
	}

}
