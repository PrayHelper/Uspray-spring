package com.uspray.uspray.domain.pray.service;

import com.uspray.uspray.domain.group.service.ScrapAndHeartService;
import com.uspray.uspray.domain.history.service.HistoryService;
import com.uspray.uspray.domain.member.service.MemberService;
import com.uspray.uspray.domain.pray.dto.pray.PrayListResponseDto;
import com.uspray.uspray.domain.pray.dto.pray.request.PrayRequestDto;
import com.uspray.uspray.domain.pray.dto.pray.request.PrayUpdateRequestDto;
import com.uspray.uspray.domain.pray.dto.pray.response.PrayResponseDto;
import com.uspray.uspray.global.enums.PrayType;
import com.uspray.uspray.domain.category.model.Category;
import com.uspray.uspray.domain.history.model.History;
import com.uspray.uspray.domain.member.model.Member;
import com.uspray.uspray.global.push.model.NotificationLog;
import com.uspray.uspray.domain.pray.model.Pray;
import com.uspray.uspray.global.exception.ErrorStatus;
import com.uspray.uspray.global.exception.model.CustomException;
import com.uspray.uspray.global.exception.model.NotFoundException;
import com.uspray.uspray.domain.category.repository.CategoryRepository;
import com.uspray.uspray.domain.pray.repository.PrayRepository;
import com.uspray.uspray.global.push.service.FCMNotificationService;
import com.uspray.uspray.global.push.service.NotificationLogService;
import java.time.LocalDate;
import java.util.List;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PrayFacade {

    private final MemberService memberService;
    private final PrayRepository prayRepository;
    private final CategoryRepository categoryRepository;
    private final HistoryService historyService;
    private final NotificationLogService notificationLogService;
    private final FCMNotificationService fcmNotificationService;
    private final ScrapAndHeartService scrapAndHeartService;
    private final ShareService shareService;

    @Transactional
    public PrayResponseDto createPray(PrayRequestDto prayRequestDto, String username) {
        Member member = memberService.findMemberByUserId(username);
        Category category = categoryRepository.getCategoryByIdAndMember(
            prayRequestDto.getCategoryId(),
            member);

        Pray pray = prayRequestDto.toEntity(member, category);
        prayRepository.save(pray);
        return PrayResponseDto.of(pray);
    }

    @Transactional
    public PrayResponseDto createPray(PrayRequestDto prayRequestDto, String username, LocalDate startDate) {
        Member member = memberService.findMemberByUserId(username);
        Category category = categoryRepository.getCategoryByIdAndMember(
            prayRequestDto.getCategoryId(),
            member);
        Pray pray = prayRequestDto.toEntity(member, category, startDate);
        prayRepository.save(pray);
        return PrayResponseDto.of(pray);
    }

    @Transactional
    public PrayResponseDto updatePray(Long prayId, String username,
        PrayUpdateRequestDto prayUpdateRequestDto) {
        Pray pray = prayRepository.getPrayByIdAndMemberId(prayId, username);

        // 그룹 기도 제목은 이 API로 수정 불가능
        checkGroupPray(pray);

        // 이 기도 제목을 공유한 적 없거나, 공유 받은 사람이 없으면 전부 수정 가능
        // 이 기도 제목을 공유한 적 있고, 누구라도 공유 받은 사람이 있으면 기도제목 내용 수정 불가능
        Pray sharedPray = prayRepository.getPrayByOriginPrayId(prayId);
        Category category = null;

        if (prayUpdateRequestDto.getCategoryId() != null) {
            category = categoryRepository.getCategoryByIdAndMember(
                prayUpdateRequestDto.getCategoryId(),
                pray.getMember());
            // 기도 제목 타입과 카테고리 타입 일치하는 지 확인
            if (!pray.getPrayType().toString().equals(category.getCategoryType().toString())) {
                throw new CustomException(ErrorStatus.PRAY_CATEGORY_TYPE_MISMATCH);
            }
        }
        pray.update(prayUpdateRequestDto, checkIsShared(sharedPray, pray), category);

        return PrayResponseDto.of(pray);
    }

    public boolean checkIsShared(Pray sharedPray, Pray pray) {
        return sharedPray != null || pray.getPrayType() == PrayType.SHARED;
    }

    public void checkGroupPray(Pray pray) {
        if (pray.getPrayType() == PrayType.GROUP) {
            throw new CustomException(ErrorStatus.PRAY_UNAUTHORIZED_EXCEPTION);
        }
    }

    @Transactional
    public void convertPrayToHistory() {
        List<Pray> prayList = prayRepository.findAllByDeadlineBefore(LocalDate.now());
        for (Pray pray : prayList) {
            pray.complete();
            Integer sharedCount = prayRepository.getSharedCountByOriginPrayId(
                pray.getOriginPrayId());
            History history = History.builder()
                .pray(pray)
                .totalCount(sharedCount) //sharedCount에 내 count도 포함되어 있음
                .build();
            historyService.saveHistory(history);
            prayRepository.delete(pray);
        }
    }

    @Transactional
    public void convertPrayToHistory(Pray pray) {
        pray.complete();
        History history = History.builder()
            .pray(pray)
            .build();
        historyService.saveHistory(history);
        prayRepository.delete(pray);
    }


    @Transactional
    public List<PrayListResponseDto> todayPray(Long prayId, String username) {
        Pray pray = prayRepository.getPrayByIdAndMemberId(prayId, username);
        handlePrayedToday(pray);
        return getPrayList(username, pray.getPrayType().stringValue());
    }

    private void sendNotification(Member member) {
        try {
            fcmNotificationService.sendMessageTo(
                member.getFirebaseToken(),
                "💘",
                "누군가가 당신의 기도제목을 두고 기도했어요");
        } catch (Exception e) {
            log.error(e.getMessage());

        }
        log.error(
            "send notification to " + member
        );
    }

    private void saveNotificationLog(Pray pray, Member member) {
        NotificationLog notificationLog = NotificationLog.builder()
            .pray(pray)
            .member(member)
            .title("누군가가 당신의 기도제목을 두고 기도했어요")
            .build();

        notificationLogService.saveNotificationLog(notificationLog);
    }

    private void handlePrayedToday(Pray pray) {
        if (pray.getLastPrayedAt().equals(LocalDate.now())) {
            throw new NotFoundException(ErrorStatus.ALREADY_PRAYED_TODAY);
        }
        pray.countUp();

        if (pray.getPrayType() == PrayType.SHARED) {
            Member originMember = memberService.findMemberById(pray.getOriginMemberId());
            if (originMember.getSecondNotiAgree()) {
                sendNotification(originMember);
                saveNotificationLog(pray, originMember);
            }
        }
    }

    @Transactional
    public PrayResponseDto deletePray(Long prayId, String username) {
        Pray pray = prayRepository.getPrayByIdAndMemberId(prayId, username);
        Member member = memberService.findMemberByUserId(username);

        scrapAndHeartService.deleteScrapAndHeart(member, pray);
        shareService.deleteByOriginPray(pray);
        prayRepository.delete(pray);
        return PrayResponseDto.of(pray);
    }

    @Transactional
    public List<PrayListResponseDto> getPrayList(String username, String prayType) {
        return categoryRepository.findAllWithOrderAndType(username, prayType);
    }

    @Transactional
    public List<PrayListResponseDto> completePray(Long prayId, String username) {
        Pray pray = prayRepository.getPrayByIdAndMemberId(prayId, username);
        pray.complete();

        createHistory(pray);
        prayRepository.delete(pray);

        return getPrayList(username, pray.getPrayType().stringValue());
    }

    private void createHistory(Pray pray) {
        Integer sharedCount = prayRepository.getSharedCountByOriginPrayId(pray.getId());
        History history = History.builder()
            .pray(pray)
            .totalCount(sharedCount)
            .build();
        historyService.saveHistory(history);
    }

    @Transactional
    public List<PrayListResponseDto> cancelPray(Long prayId, String username) {
        return getPrayList(username,
            prayRepository.cancelPray(prayId, username).getPrayType().stringValue());
    }
}
