package com.uspray.uspray.service;

import com.uspray.uspray.Enums.NotificationType;
import com.uspray.uspray.infrastructure.query.MemberQueryRepository;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SchedulerService {

    private final MemberQueryRepository memberQueryRepository;
    private final FCMNotificationService fcmNotificationService;

    @Scheduled(cron = "0 0 8 * * *")
    public void pushPrayNotification() throws IOException {
        List<String> deviceTokens = memberQueryRepository.getDeviceTokensByFirstNotiAgree(
            true);
        for (String device : deviceTokens) {
            fcmNotificationService.sendMessageTo(device, NotificationType.PRAY_TIME.getTitle(), NotificationType.PRAY_TIME.getBody());
        }
    }
}
