package com.uspray.uspray.service;

import com.uspray.uspray.Enums.NotificationType;
import com.uspray.uspray.infrastructure.query.MemberQueryRepository;
import com.uspray.uspray.service.facade.PrayFacade;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SchedulerService {

    private final MemberQueryRepository memberQueryRepository;
    private final FCMNotificationService fcmNotificationService;
    private final ShareService shareService;
    private final PrayFacade prayFacadeService;

    @Scheduled(cron = "0 0 8 * * *")
    public void pushPrayNotification() throws IOException {
        List<String> deviceTokens = memberQueryRepository.getDeviceTokensByFirstNotiAgree(
            true);
        for (String device : deviceTokens) {
            fcmNotificationService.sendMessageTo(device, NotificationType.PRAY_TIME.getTitle(),
                NotificationType.PRAY_TIME.getBody());
        }
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void cleanSharedPray() {
        LocalDate thresholdDate = LocalDate.now().minusDays(15);
        shareService.cleanSharedPray(thresholdDate);
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void convertPrayToHistory() {
        prayFacadeService.convertPrayToHistory();
    }
}
