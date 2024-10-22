package com.uspray.uspray.domain.group.service;

import com.uspray.uspray.domain.category.model.Category;
import com.uspray.uspray.domain.category.repository.CategoryRepository;
import com.uspray.uspray.domain.group.dto.grouppray.GroupPrayRappingDto;
import com.uspray.uspray.domain.group.dto.grouppray.GroupPrayRequestDto;
import com.uspray.uspray.domain.group.dto.grouppray.GroupPrayResponseDto;
import com.uspray.uspray.domain.group.dto.grouppray.ScrapRequestDto;
import com.uspray.uspray.domain.group.model.Group;
import com.uspray.uspray.domain.group.model.GroupMember;
import com.uspray.uspray.domain.group.model.GroupPray;
import com.uspray.uspray.domain.group.model.ScrapAndHeart;
import com.uspray.uspray.domain.group.repository.GroupMemberRepository;
import com.uspray.uspray.domain.group.repository.GroupPrayRepository;
import com.uspray.uspray.domain.group.repository.GroupRepository;
import com.uspray.uspray.domain.group.repository.ScrapAndHeartRepository;
import com.uspray.uspray.domain.member.model.Member;
import com.uspray.uspray.domain.member.service.MemberService;
import com.uspray.uspray.domain.pray.dto.pray.PrayListResponseDto;
import com.uspray.uspray.domain.pray.dto.pray.request.PrayToGroupPrayDto;
import com.uspray.uspray.domain.pray.model.Pray;
import com.uspray.uspray.domain.pray.repository.PrayRepository;
import com.uspray.uspray.global.enums.PrayType;
import com.uspray.uspray.global.exception.ErrorStatus;
import com.uspray.uspray.global.exception.model.CustomException;
import com.uspray.uspray.global.push.model.NotificationLog;
import com.uspray.uspray.global.push.repository.NotificationLogRepository;
import com.uspray.uspray.global.push.service.FCMNotificationService;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupPrayFacade {

    private final GroupPrayRepository groupPrayRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final ScrapAndHeartRepository scrapAndHeartRepository;
    private final CategoryRepository categoryRepository;
    private final PrayRepository prayRepository;
    private final GroupRepository groupRepository;
    private final NotificationLogRepository notificationLogRepository;
    private final FCMNotificationService fcmNotificationService;
    private final MemberService memberService;

    @Transactional
    public void prayToGroupPray(PrayToGroupPrayDto prayToGroupPrayDto, String userId) {
        Member member = memberService.findMemberByUserId(userId);
        Group group = groupRepository.getGroupById(prayToGroupPrayDto.getGroupId());

        List<Long> existIds = group.getGroupPrayList().stream()
            .map(gp -> gp.getOriginPray().getId())
            .collect(Collectors.toList());
        if (existIds.stream().anyMatch(id -> id.equals(prayToGroupPrayDto.getPrayId()))) {
            throw new CustomException(ErrorStatus.ALREADY_EXIST_GROUP_PRAY_EXCEPTION);
        }

        List<Pray> mainPray = prayRepository.findAllByIdIn(prayToGroupPrayDto.getPrayId());
        List<Pray> targetPray = prayRepository.findAllByOriginPrayIdIn(
            prayToGroupPrayDto.getPrayId());

        for (Pray p : mainPray) {
            GroupPray groupPray = GroupPray.builder()
                .group(group)
                .author(member)
                .content(new String(Base64.getDecoder().decode(p.getContent().getBytes())))
                .deadline(p.getDeadline())
                .build();
            p.setGroupPray(groupPray);
            groupPrayRepository.save(groupPray);
            p.setIsShared();

            ScrapAndHeart scrapAndHeart = ScrapAndHeart.builder()
                .groupPray(groupPray)
                .member(member)
                .build();
            scrapAndHeartRepository.save(scrapAndHeart);

            for (Pray TP : targetPray) {
                ScrapAndHeart targetScrapAndHeart = ScrapAndHeart.builder()
                    .groupPray(groupPray)
                    .member(TP.getMember())
                    .build();
                targetScrapAndHeart.scrapPray(TP);
                scrapAndHeartRepository.save(targetScrapAndHeart);
            }
        }
    }

    public List<PrayListResponseDto> getPrayList(String username, String prayType, Long groupId) {
        List<Long> prayIds = groupPrayRepository.getOriginPrayIdByGroupId(groupId);
        return categoryRepository.findAllWithOrderAndType(username, prayType, prayIds);
    }


    @Transactional
    public void createGroupPray(GroupPrayRequestDto groupPrayRequestDto, String userId) {
        Member author = memberService.findMemberByUserId(userId);
        Group group = groupRepository.getGroupById(groupPrayRequestDto.getGroupId());
        GroupPray groupPray = GroupPray.of(group, author, groupPrayRequestDto.getDeadline(),
            groupPrayRequestDto.getContent());

        groupPrayRepository.save(groupPray);
        scrapAndHeartRepository.save(ScrapAndHeart.createdByGroupPrayOf(groupPray, author));

        Category category = categoryRepository.getCategoryByIdAndMember(
            groupPrayRequestDto.getCategoryId(),
            author);

        prayRepository.save(Pray.createdByGroupPrayOf(author, groupPrayRequestDto.getContent(),
            groupPrayRequestDto.getDeadline(), category, PrayType.PERSONAL, groupPray, true));
    }

    @Transactional(readOnly = true)
    public GroupPrayRappingDto getGroupPray(Long groupId, String userId) {

        Member member = memberService.findMemberByUserId(userId);
        Group group = groupRepository.getGroupById(groupId);
        GroupMember groupMember = groupMemberRepository.getGroupMemberByGroupAndMember(group,
            member);
        List<GroupPray> groupPrays = groupPrayRepository.findGroupPraysByGroup(group);

        Long count = scrapAndHeartRepository.countHeart(groupPrays, true);

        List<GroupPrayResponseDto> groupPrayList = new ArrayList<>();

        for (GroupPray groupPray : groupPrays) {
            Optional<ScrapAndHeart> SH = scrapAndHeartRepository.findScrapAndHeartByGroupPrayAndMember(
                groupPray, member);
            if (SH.isPresent()) {
                ScrapAndHeart scrapAndHeart = SH.get();
                groupPrayList.add(GroupPrayResponseDto.builder()
                    .groupPray(groupPray)
                    .member(member)
                    .scrapAndHeart(scrapAndHeart)
                    .build());
                continue;
            }
            groupPrayList.add(GroupPrayResponseDto.builder()
                .groupPray(groupPray)
                .member(member)
                .build());
        }

        // 역순으로 정렬하는 트리맵 정의
        Map<LocalDate, List<GroupPrayResponseDto>> sortedMap = new TreeMap<>(
            Comparator.reverseOrder());

        // 트리맵에 날짜별로 그룹화 된 기도제목 리스트 입력
        sortedMap.putAll(groupPrayList.stream()
            .collect(Collectors.groupingBy(GroupPrayResponseDto::getCreatedAt)));

        // 날짜별로 그룹화 된 기도제목을 기도제목 id 순으로 정렬
        for (Map.Entry<LocalDate, List<GroupPrayResponseDto>> entry : sortedMap.entrySet()) {
            List<GroupPrayResponseDto> value = entry.getValue();
            value.sort(Comparator.comparing(GroupPrayResponseDto::getGroupPrayId).reversed());
        }

        return new GroupPrayRappingDto(count, groupMember.getNotificationAgree(), sortedMap);
    }

    @Transactional
    public void heartGroupPray(Long groupPrayId, String userId) {
        GroupPray groupPray = groupPrayRepository.getGroupPrayById(groupPrayId);
        Member member = memberService.findMemberByUserId(userId);

        Optional<ScrapAndHeart> scrapAndHeartByGroupPrayAndMember = scrapAndHeartRepository.findScrapAndHeartByGroupPrayAndMember(
            groupPray, member);

        if (scrapAndHeartByGroupPrayAndMember.isEmpty()) {
            ScrapAndHeart scrapAndHeart = ScrapAndHeart.builder()
                .groupPray(groupPray)
                .member(member)
                .build();
            scrapAndHeart.heartPray();
            scrapAndHeartRepository.save(scrapAndHeart);
            sendNotificationAndSaveLog(scrapAndHeart, groupPray, groupPray.getAuthor(), true);
            return;
        }
        scrapAndHeartByGroupPrayAndMember.get().heartPray();
        sendNotificationAndSaveLog(scrapAndHeartByGroupPrayAndMember.get(), groupPray,
            groupPray.getAuthor(), true);
    }

    @Transactional
    public void scrapGroupPray(ScrapRequestDto scrapRequestDto, String userId) {
        Category category = categoryRepository.getCategoryById(scrapRequestDto.getCategoryId());
        if (!category.getCategoryType().toString().equals(PrayType.SHARED.toString())) {
            throw new CustomException(ErrorStatus.PRAY_CATEGORY_TYPE_MISMATCH);
        }
        GroupPray groupPray = groupPrayRepository.getGroupPrayById(
            scrapRequestDto.getGroupPrayId());
        Member member = memberService.findMemberByUserId(userId);

        Optional<ScrapAndHeart> scrapAndHeartByGroupPrayAndMember = scrapAndHeartRepository.findScrapAndHeartByGroupPrayAndMember(
            groupPray, member);
        Pray originPray = prayRepository.getPrayById(groupPray.getOriginPray().getId());
        originPray.setIsShared();

        if (scrapAndHeartByGroupPrayAndMember.isEmpty()) {
            Pray pray = makePray(scrapRequestDto, groupPray, member);

            ScrapAndHeart scrapAndHeart = ScrapAndHeart.builder()
                .groupPray(groupPray)
                .member(member)
                .sharedPray(pray)
                .build();
            prayRepository.save(pray);
            scrapAndHeart.scrapPray(pray);
            scrapAndHeartRepository.save(scrapAndHeart);
            sendNotificationAndSaveLog(scrapAndHeart, groupPray, groupPray.getAuthor(), false);
            return;
        }
        Pray pray = makePray(scrapRequestDto, groupPray, member);
        prayRepository.save(pray);
        scrapAndHeartByGroupPrayAndMember.get().scrapPray(pray);
        sendNotificationAndSaveLog(scrapAndHeartByGroupPrayAndMember.get(), groupPray,
            groupPray.getAuthor(), false);
    }

    private Pray makePray(ScrapRequestDto scrapRequestDto, GroupPray groupPray, Member member) {
        Category category = categoryRepository.getCategoryByIdAndMember(
            scrapRequestDto.getCategoryId(),
            member);

        return Pray.builder()
            .content(groupPray.getContent())
            .deadline(scrapRequestDto.getDeadline())
            .member(member)
            .originMemberId(groupPray.getAuthor().getId())
            .originPrayId(groupPray.getOriginPray().getId())
            .category(category)
            .prayType(PrayType.SHARED)
            .build();
    }

    private void sendNotificationAndSaveLog(ScrapAndHeart scrapAndHeart, GroupPray groupPray,
        Member receiver, boolean isHeart) throws IOException {
        String groupName = groupPray.getGroup().getName();
        String name = scrapAndHeart.getMember().getName();

        String title = isHeart ? groupName + " 💘" : groupName + " 💌 ";
        String body = isHeart ? name + "님이 당신의 기도제목을 두고 기도했어요" : name + "님이 당신의 기도제목을 저장했어요";

        fcmNotificationService.sendMessageTo(receiver.getFirebaseToken(), title, body);
        fcmNotificationService.saveNotificationLog(
            NotificationLog.of(memberService.findMemberByUserId(receiver.getUserId()),
                groupPray.getOriginPray(), body));

    }
}
