package com.uspray.uspray.domain.pray.service;

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
import com.uspray.uspray.domain.group.model.ScrapAndHeart;
import com.uspray.uspray.global.exception.ErrorStatus;
import com.uspray.uspray.global.exception.model.CustomException;
import com.uspray.uspray.global.exception.model.NotFoundException;
import com.uspray.uspray.domain.category.repository.CategoryRepository;
import com.uspray.uspray.domain.history.repository.HistoryRepository;
import com.uspray.uspray.domain.member.repository.MemberRepository;
import com.uspray.uspray.global.push.repository.NotificationLogRepository;
import com.uspray.uspray.domain.pray.repository.PrayRepository;
import com.uspray.uspray.domain.group.repository.ScrapAndHeartRepository;
import com.uspray.uspray.global.push.service.FCMNotificationService;
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

    private final MemberRepository memberRepository;
    private final PrayRepository prayRepository;
    private final CategoryRepository categoryRepository;
    private final HistoryRepository historyRepository;
    private final NotificationLogRepository notificationLogRepository;
    private final FCMNotificationService fcmNotificationService;
    private final ScrapAndHeartRepository scrapAndHeartRepository;
    private final ShareService shareService;

    @Transactional
    public PrayResponseDto createPray(PrayRequestDto prayRequestDto, String username) {
        Member member = memberRepository.getMemberByUserId(username);
        Category category = categoryRepository.getCategoryByIdAndMember(
            prayRequestDto.getCategoryId(),
            member);

        Pray pray = prayRequestDto.toEntity(member, category);
        prayRepository.save(pray);
        return PrayResponseDto.of(pray);
    }

    @Transactional
    public PrayResponseDto createPray(PrayRequestDto prayRequestDto, String username, LocalDate startDate) {
        Member member = memberRepository.getMemberByUserId(username);
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
        categoryRepository.getCategoryByIdAndMember(
            prayUpdateRequestDto.getCategoryId(),
            pray.getMember());

        // 그룹 기도 제목은 이 API로 수정 불가능
        checkGroupPray(pray);

        // 이 기도 제목을 공유한 적 없거나, 공유 받은 사람이 없으면 전부 수정 가능
        // 이 기도 제목을 공유한 적 있고, 누구라도 공유 받은 사람이 있으면 기도제목 내용 수정 불가능
        Pray sharedPray = prayRepository.getPrayByOriginPrayId(prayId);
        if (prayUpdateRequestDto.getCategoryId() != null) {
            Category category = categoryRepository.getCategoryByIdAndMember(
                prayUpdateRequestDto.getCategoryId(),
                pray.getMember());
            // 기도 제목 타입과 카테고리 타입 일치하는 지 확인
            if (!pray.getPrayType().toString().equals(category.getCategoryType().toString())) {
                throw new CustomException(ErrorStatus.PRAY_CATEGORY_TYPE_MISMATCH);
            }
            pray.update(prayUpdateRequestDto,
                checkIsShared(sharedPray, pray), category);
        }

        pray.update(prayUpdateRequestDto,
            checkIsShared(sharedPray, pray), null);

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
            historyRepository.save(history);
            prayRepository.delete(pray);
        }
    }

    @Transactional
    public void convertPrayToHistory(Pray pray) {
        pray.complete();
        History history = History.builder()
            .pray(pray)
            .build();
        historyRepository.save(history);
        prayRepository.delete(pray);
    }


    public void createHistory(String username, Long prayId) {
        Pray pray = prayRepository.getPrayByIdAndMemberId(prayId, username);
        pray.complete();
        Integer sharedCount = prayRepository.getSharedCountByOriginPrayId(prayId);
        History history = History.builder()
            .pray(pray)
            .totalCount(pray.getCount() + sharedCount)
            .build();
        historyRepository.save(history);
    }

    @Transactional
    public List<PrayListResponseDto> todayPray(Long prayId, String username) {
        Pray pray = prayRepository.getPrayByIdAndMemberId(prayId, username);
        handlePrayedToday(pray);
        return getPrayList(username, pray.getPrayType().stringValue());
    }

    private void sendNotificationAndSaveLog(Pray pray, Member member) {
        try {
            fcmNotificationService.sendMessageTo(
                member.getFirebaseToken(),
                "💘",
                "누군가가 당신의 기도제목을 두고 기도했어요");
        } catch (Exception e) {
            log.error(e.getMessage());

        }
        log.error(
            "send notification to " + memberRepository.getMemberByUserId(member.getUserId())
        );
        NotificationLog notificationLog = NotificationLog.builder()
            .pray(pray)
            .member(memberRepository.getMemberByUserId(member.getUserId()))
            .title("누군가가 당신의 기도제목을 두고 기도했어요")
            .build();
        notificationLogRepository.save(notificationLog);
    }

    private void handlePrayedToday(Pray pray) {
        if (pray.getLastPrayedAt().equals(LocalDate.now())) {
            throw new NotFoundException(ErrorStatus.ALREADY_PRAYED_TODAY);
        }
        pray.countUp();

        if (pray.getPrayType() == PrayType.SHARED) {
            Member originMember = memberRepository.getMemberById(pray.getOriginMemberId());
            if (originMember.getSecondNotiAgree()) {
                sendNotificationAndSaveLog(pray, originMember);
            }
        }
    }

    @Transactional
    public PrayResponseDto deletePray(Long prayId, String username) {
        Pray pray = prayRepository.getPrayByIdAndMemberId(prayId, username);
        Member member = memberRepository.getMemberByUserId(username);
        ScrapAndHeart scrapAndHeart = scrapAndHeartRepository.findByMemberAndSharedPray(member,
            pray);
        if (scrapAndHeart != null) {
            scrapAndHeart.deletePrayId();
        }
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
        prayRepository.delete(pray);

        return getPrayList(username, pray.getPrayType().stringValue());
    }

    @Transactional
    public List<PrayListResponseDto> cancelPray(Long prayId, String username) {
        return getPrayList(username,
            prayRepository.cancelPray(prayId, username).getPrayType().stringValue());
    }
}
