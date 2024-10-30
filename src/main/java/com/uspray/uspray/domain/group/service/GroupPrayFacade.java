package com.uspray.uspray.domain.group.service;

import com.uspray.uspray.domain.category.model.Category;
import com.uspray.uspray.domain.category.service.CategoryService;
import com.uspray.uspray.domain.group.dto.grouppray.GroupPrayRappingDto;
import com.uspray.uspray.domain.group.dto.grouppray.GroupPrayRequestDto;
import com.uspray.uspray.domain.group.dto.grouppray.GroupPrayResponseDto;
import com.uspray.uspray.domain.group.dto.grouppray.ScrapRequestDto;
import com.uspray.uspray.domain.group.model.Group;
import com.uspray.uspray.domain.group.model.GroupMember;
import com.uspray.uspray.domain.group.model.GroupPray;
import com.uspray.uspray.domain.group.model.ScrapAndHeart;
import com.uspray.uspray.domain.member.model.Member;
import com.uspray.uspray.domain.member.service.MemberService;
import com.uspray.uspray.domain.pray.dto.pray.PrayListResponseDto;
import com.uspray.uspray.domain.pray.dto.pray.request.PrayToGroupPrayDto;
import com.uspray.uspray.domain.pray.model.Pray;
import com.uspray.uspray.domain.pray.service.PrayService;
import com.uspray.uspray.global.enums.CategoryType;
import com.uspray.uspray.global.enums.PrayType;
import com.uspray.uspray.global.exception.ErrorStatus;
import com.uspray.uspray.global.exception.model.CustomException;
import com.uspray.uspray.global.push.model.NotificationLog;
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

    private final FCMNotificationService fcmNotificationService;
    private final MemberService memberService;
    private final PrayService prayService;
    private final GroupPrayService groupPrayService;
    private final GroupMemberService groupMemberService;
    private final GroupService groupService;
    private final CategoryService categoryService;
    private final ScrapAndHeartService scrapAndHeartService;

    @Transactional
    public void prayToGroupPray(PrayToGroupPrayDto prayToGroupPrayDto, String userId) {
        Member member = memberService.findMemberByUserId(userId);
        Group group = groupService.getGroupById(prayToGroupPrayDto.getGroupId());

        List<Long> existIds = group.getGroupPrayList().stream()
            .map(gp -> gp.getOriginPray().getId())
            .collect(Collectors.toList());
        if (existIds.stream().anyMatch(id -> id.equals(prayToGroupPrayDto.getPrayId()))) {
            throw new CustomException(ErrorStatus.ALREADY_EXIST_GROUP_PRAY_EXCEPTION);
        }

        List<Pray> mainPray = prayService.findAllByIdIn(prayToGroupPrayDto.getPrayId());
        List<Pray> targetPray = prayService.findAllByOriginPrayIdIn(
            prayToGroupPrayDto.getPrayId());

        for (Pray p : mainPray) {
            GroupPray groupPray = GroupPray.builder()
                .group(group)
                .author(member)
                .content(new String(Base64.getDecoder().decode(p.getContent().getBytes())))
                .deadline(p.getDeadline())
                .build();
            p.setGroupPray(groupPray);
            groupPrayService.saveGroupPray(groupPray);
            p.setIsShared();

            ScrapAndHeart scrapAndHeart = ScrapAndHeart.builder()
                .groupPray(groupPray)
                .member(member)
                .build();
            scrapAndHeartService.save(scrapAndHeart);

            for (Pray TP : targetPray) {
                ScrapAndHeart targetScrapAndHeart = ScrapAndHeart.builder()
                    .groupPray(groupPray)
                    .member(TP.getMember())
                    .build();
                targetScrapAndHeart.scrapPray(TP);
                scrapAndHeartService.save(targetScrapAndHeart);
            }
        }
    }

    public List<PrayListResponseDto> getPrayList(String username, String prayType, Long groupId) {
        List<Long> prayIds = groupPrayService.getOriginPrayIdsByGroupId(groupId);
        return categoryService.findAllWithOrderAndType(username, prayType, prayIds);
    }


    @Transactional
    public void createGroupPray(GroupPrayRequestDto groupPrayRequestDto, String userId) {
        Member author = memberService.findMemberByUserId(userId);
        Group group = groupService.getGroupById(groupPrayRequestDto.getGroupId());
        GroupPray groupPray = GroupPray.of(group, author, groupPrayRequestDto.getDeadline(),
            groupPrayRequestDto.getContent());

        groupPrayService.saveGroupPray(groupPray);
        scrapAndHeartService.save(ScrapAndHeart.createdByGroupPrayOf(groupPray, author));

        Category category = categoryService.getCategoryByIdAndMemberAndType(
            groupPrayRequestDto.getCategoryId(), author, CategoryType.PERSONAL);

        prayService.savePray(Pray.createdByGroupPrayOf(author, groupPrayRequestDto.getContent(),
            groupPrayRequestDto.getDeadline(), category, PrayType.PERSONAL, groupPray, true));
    }

    @Transactional(readOnly = true)
    public GroupPrayRappingDto getGroupPray(Long groupId, String userId) {

        Member member = memberService.findMemberByUserId(userId);
        Group group = groupService.getGroupById(groupId);

        List<GroupPray> groupPrays = groupPrayService.findGroupPraysByGroup(group);
        Long count = scrapAndHeartService.countHeart(groupPrays);

        List<GroupPrayResponseDto> groupPrayList = new ArrayList<>();
        for (GroupPray groupPray : groupPrays) {
            ScrapAndHeart scrapAndHeart = scrapAndHeartService.findScrapAndHeartByGroupPrayAndMember(
                groupPray, member).orElse(null);
            groupPrayList.add(GroupPrayResponseDto.builder()
                .groupPray(groupPray)
                .member(member)
                .scrapAndHeart(scrapAndHeart)
                .build());
        }

        GroupMember groupMember = groupMemberService.getGroupMemberByGroupAndMember(group, member);

        return new GroupPrayRappingDto(count, groupMember.getNotificationAgree(),
            makeSortedTreeMap(groupPrayList));
    }

    private Map<LocalDate, List<GroupPrayResponseDto>> makeSortedTreeMap(
        List<GroupPrayResponseDto> groupPrayList) {
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
        return sortedMap;
    }

    @Transactional
    public void heartGroupPray(Long groupPrayId, String userId) throws IOException {
        GroupPray groupPray = groupPrayService.getGroupPrayById(groupPrayId);
        Member member = memberService.findMemberByUserId(userId);

        ScrapAndHeart scrapAndHeart = scrapAndHeartService.findScrapAndHeartByGroupPrayAndMember(groupPray, member)
            .orElse(ScrapAndHeart.builder()
                .groupPray(groupPray)
                .member(member)
                .build());

        scrapAndHeart.heartPray();
        scrapAndHeartService.save(scrapAndHeart);
        sendNotificationAndSaveLog(scrapAndHeart, groupPray, groupPray.getAuthor(), true);
    }

    @Transactional
    public void scrapGroupPray(ScrapRequestDto scrapRequestDto, String userId) throws IOException {
        Member member = memberService.findMemberByUserId(userId);
        Category category = categoryService.getCategoryByIdAndMemberAndType(scrapRequestDto.getCategoryId(), member, CategoryType.SHARED);
        GroupPray groupPray = groupPrayService.getGroupPrayById(
            scrapRequestDto.getGroupPrayId());

        Pray pray = makePray(scrapRequestDto, groupPray, member, category);
        prayService.savePray(pray);

        Optional<ScrapAndHeart> scrapAndHeartByGroupPrayAndMember = scrapAndHeartService.findScrapAndHeartByGroupPrayAndMember(
            groupPray, member);

        ScrapAndHeart scrapAndHeart = checkScrapAndHeartExist(scrapAndHeartByGroupPrayAndMember,
            groupPray, member, pray);
        sendNotificationAndSaveLog(scrapAndHeart, groupPray,
            groupPray.getAuthor(), false);
    }

    private ScrapAndHeart checkScrapAndHeartExist(
        Optional<ScrapAndHeart> scrapAndHeartByGroupPrayAndMember, GroupPray groupPray,
        Member member, Pray pray) {
        if (scrapAndHeartByGroupPrayAndMember.isEmpty()) {

            ScrapAndHeart scrapAndHeart = ScrapAndHeart.createdByScrapOf(groupPray, member, pray);
            scrapAndHeartService.save(scrapAndHeart);
            return scrapAndHeart;
        }
        ScrapAndHeart scrapAndHeart = scrapAndHeartByGroupPrayAndMember.get();
        scrapAndHeart.scrapPray(pray);
        return scrapAndHeart;
    }

    private Pray makePray(ScrapRequestDto scrapRequestDto, GroupPray groupPray, Member member, Category category) {
        groupPray.getOriginPray().setIsShared();

        return Pray.createdByScrapOf(member, groupPray.getContent(), scrapRequestDto.getDeadline(),
            groupPray.getAuthor().getId(), groupPray.getOriginPray().getId(), category,
            PrayType.SHARED);
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
