package com.uspray.uspray.domain.pray.service;

import com.uspray.uspray.domain.category.dto.CategoryResponseDto;
import com.uspray.uspray.domain.category.model.Category;
import com.uspray.uspray.domain.category.service.CategoryService;
import com.uspray.uspray.domain.group.service.ScrapAndHeartService;
import com.uspray.uspray.domain.history.model.History;
import com.uspray.uspray.domain.history.service.HistoryService;
import com.uspray.uspray.domain.member.model.Member;
import com.uspray.uspray.domain.member.service.MemberService;
import com.uspray.uspray.domain.pray.dto.pray.PrayListResponseDto;
import com.uspray.uspray.domain.pray.dto.pray.request.PrayRequestDto;
import com.uspray.uspray.domain.pray.dto.pray.request.PrayUpdateRequestDto;
import com.uspray.uspray.domain.pray.dto.pray.response.PrayResponseDto;
import com.uspray.uspray.domain.pray.model.Pray;
import com.uspray.uspray.global.enums.CategoryType;
import com.uspray.uspray.global.enums.PrayType;
import com.uspray.uspray.global.exception.ErrorStatus;
import com.uspray.uspray.global.exception.model.CustomException;
import com.uspray.uspray.global.exception.model.NotFoundException;
import com.uspray.uspray.global.push.model.NotificationLog;
import com.uspray.uspray.global.push.service.FCMNotificationService;
import com.uspray.uspray.global.push.service.NotificationLogService;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PrayFacade {
    private final HistoryService historyService;
    private final NotificationLogService notificationLogService;
    private final FCMNotificationService fcmNotificationService;
    private final ScrapAndHeartService scrapAndHeartService;
    private final ShareService shareService;
    private final PrayService prayService;
    private final MemberService memberService;
    private final CategoryService categoryService;

    @Transactional
    public PrayResponseDto createPray(PrayRequestDto prayRequestDto, String username,
        LocalDate startDateOrNull) {
        Member member = memberService.findMemberByUserId(username);
        Category category = categoryService.getCategoryByIdAndMember(prayRequestDto.getCategoryId(),
            member);
        return prayService.savePray(prayRequestDto.toEntity(member, category, startDateOrNull));
    }

    @Transactional
    public PrayResponseDto updatePray(Long prayId, String username,
        PrayUpdateRequestDto prayUpdateRequestDto) {
        Pray pray = prayService.getPrayByIdAndMemberId(prayId, username);

        // 이 기도 제목을 공유한 적 없거나, 공유 받은 사람이 없으면 전부 수정 가능
        // 이 기도 제목을 공유한 적 있고, 누구라도 공유 받은 사람이 있으면 기도제목 내용 수정 불가능
        Pray sharedPray = prayService.getSharedPray(prayId);
        Category category = categoryService.getCategoryByIdAndMember(
            prayUpdateRequestDto.getCategoryId(),
            pray.getMember());
        // 기도 제목 타입과 카테고리 타입 일치하는 지 확인
        if (!pray.getPrayType().toString().equals(category.getCategoryType().toString())) {
            throw new CustomException(ErrorStatus.PRAY_CATEGORY_TYPE_MISMATCH);
        }

        // 공유 됐을 때 content가 있는 경우
        if ((sharedPray != null || pray.getPrayType() == PrayType.SHARED) && prayUpdateRequestDto.getContent() != null) {
            throw new CustomException(ErrorStatus.ALREADY_SHARED_EXCEPTION);
        }

        return PrayResponseDto.of(pray.update(prayUpdateRequestDto, category));
    }

    @Transactional
    public void convertPrayToHistory() {
        List<Pray> prayList = prayService.getPrayListDeadlineBefore(LocalDate.now());
        for (Pray pray : prayList) {
            pray.complete();
            Integer sharedCount = prayService.getSharedCountByOriginPrayId(
                pray.getOriginPrayId());
            History history = History.builder()
                .pray(pray)
                .totalCount(sharedCount) //sharedCount에 내 count도 포함되어 있음
                .build();
            historyService.saveHistory(history);
            prayService.deletePray(pray);
        }
    }

    public void convertPrayToHistory(Pray pray) {
        pray.complete();
        History history = History.builder()
            .pray(pray)
            .build();
        historyService.saveHistory(history);
        prayService.deletePray(pray);
    }

    @Transactional
    public CategoryResponseDto deleteCategory(String username, Long categoryId) {
        Member member = memberService.findMemberByUserId(username);
        Category category = categoryService.getCategoryByIdAndMember(categoryId, member);

        prayService.getPrayListByCategory(category).forEach(this::convertPrayToHistory);

        return categoryService.deleteCategory(category);
    }


    @Transactional
    public List<PrayListResponseDto> todayPray(Long prayId, String username) {
        Pray pray = prayService.getPrayByIdAndMemberId(prayId, username);
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
        Pray pray = prayService.getPrayByIdAndMemberId(prayId, username);
        Member member = memberService.findMemberByUserId(username);

        scrapAndHeartService.deleteScrapAndHeart(member, pray);
        shareService.deleteByOriginPray(pray);
        prayService.deletePray(pray);
        return PrayResponseDto.of(pray);
    }

    @Transactional
    public List<PrayListResponseDto> getPrayList(String username, String prayType) {
        // 사용자 정보와 카테고리 타입을 가져옴
        Member member = memberService.findMemberByUserId(username);
        CategoryType categoryType = CategoryType.valueOf(prayType);
        List<Category> categoryList = categoryService.getCategoryListByMemberAndCategoryType(member, categoryType);

        // 카테고리 리스트가 비어있을 경우 빈 리스트 반환
        if (categoryList.isEmpty()) {
            return Collections.emptyList();
        }

        // PrayListResponseDto 생성 및 반환
        return categoryList.stream()
            .map(category -> createPrayListResponseDto(member, category))
            .collect(Collectors.toList());
    }

    private PrayListResponseDto createPrayListResponseDto(Member member, Category category) {
        // 기도 리스트 변환
        List<PrayResponseDto> prayResponseDtos = prayService.getPrayListByMemberAndCategory(member, category).stream()
            .map(this::convertToPrayResponseDto)
            .collect(Collectors.toList());

        // PrayListResponseDto 생성
        return new PrayListResponseDto(category.getId(), category.getName(), category.getColor(), prayResponseDtos);
    }

    private PrayResponseDto convertToPrayResponseDto(Pray pray) {
        // 기도의 타입에 따라 적절한 PrayResponseDto 생성
        if (pray.getPrayType().equals(PrayType.SHARED)) {
            Member originMember = memberService.findMemberById(pray.getOriginMemberId());
            return PrayResponseDto.shared(pray, originMember);
        } else {
            return PrayResponseDto.of(pray);
        }
    }


    @Transactional
    public List<PrayListResponseDto> completePray(Long prayId, String username) {
        Pray pray = prayService.getPrayByIdAndMemberId(prayId, username);
        pray.complete();

        createHistory(pray);
        prayService.deletePray(pray);

        return getPrayList(username, pray.getPrayType().stringValue());
    }

    private void createHistory(Pray pray) {
        Integer sharedCount = prayService.getSharedCountByOriginPrayId(pray.getId());
        History history = History.builder()
            .pray(pray)
            .totalCount(sharedCount)
            .build();
        historyService.saveHistory(history);
    }

    @Transactional
    public List<PrayListResponseDto> cancelPray(Long prayId, String username) {
        return getPrayList(username,
            prayService.cancelPray(prayId, username).getPrayType().stringValue());
    }
}
