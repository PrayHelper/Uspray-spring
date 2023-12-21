package com.uspray.uspray.service;

import com.uspray.uspray.DTO.sharedpray.request.SharedPrayDeleteRequestDto;
import com.uspray.uspray.DTO.sharedpray.request.SharedPrayRequestDto;
import com.uspray.uspray.DTO.sharedpray.request.SharedPraySaveRequestDto;
import com.uspray.uspray.DTO.sharedpray.response.SharedPrayResponseDto;
import com.uspray.uspray.Enums.PrayType;
import com.uspray.uspray.domain.Category;
import com.uspray.uspray.domain.Member;
import com.uspray.uspray.domain.Pray;
import com.uspray.uspray.domain.SharedPray;
import com.uspray.uspray.exception.ErrorStatus;
import com.uspray.uspray.exception.model.CustomException;
import com.uspray.uspray.exception.model.NotFoundException;
import com.uspray.uspray.infrastructure.CategoryRepository;
import com.uspray.uspray.infrastructure.MemberRepository;
import com.uspray.uspray.infrastructure.PrayRepository;
import com.uspray.uspray.infrastructure.SharedPrayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShareFacade {

    private final SharedPrayRepository sharedPrayRepository;
    private final MemberRepository memberRepository;
    private final PrayRepository prayRepository;
    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<SharedPrayResponseDto> getSharedPrayList(String username) {

        Member member = memberRepository.getMemberByUserId(username);
        List<SharedPray> sharedPrayList = sharedPrayRepository.findAllByMemberOrderByCreatedAtDesc(member);

        return sharedPrayList.stream()
            .map(SharedPrayResponseDto::of)
            .collect(Collectors.toList());
    }

    @Transactional
    public void receivedSharedPray(String username, SharedPrayRequestDto sharedPrayRequestDto) {

        Member member = memberRepository.getMemberByUserId(username);
        List<Pray> prayList = prayRepository.findAllByIdIn(sharedPrayRequestDto.getPrayIds());

        if (prayList.size() != sharedPrayRequestDto.getPrayIds().size()) {
            throw new NotFoundException(ErrorStatus.PRAY_NOT_FOUND_EXCEPTION, ErrorStatus.PRAY_NOT_FOUND_EXCEPTION.getMessage());
        }
        for (Pray pray : prayList) {
            SharedPray sharedPray = SharedPray.builder()
                .member(member)
                .pray(pray)
                .build();
            sharedPrayRepository.save(sharedPray);
        }
    }

    @Transactional
    public void saveSharedPray(String username, SharedPraySaveRequestDto sharedPraySaveRequestDto) {

        Member member = memberRepository.getMemberByUserId(username);
        Category category = categoryRepository.getCategoryById(sharedPraySaveRequestDto.getCategoryId());
        List<Long> sharedPrayIds = sharedPraySaveRequestDto.getSharedPrayIds();

        for (Long id : sharedPrayIds) {
            save(member, id, category);
        }
    }

    private void save(Member member, Long sharedPrayId, Category category) {

        SharedPray sharedPray = sharedPrayRepository.getSharedPrayById(sharedPrayId);

        if (sharedPray.getPray().getDeleted()) {
            sharedPrayRepository.deleteById(sharedPrayId);
            throw new CustomException(ErrorStatus.PRAY_ALREADY_DELETED_EXCEPTION, ErrorStatus.PRAY_ALREADY_DELETED_EXCEPTION.getMessage());
        }
        Pray pray = Pray.builder()
            .member(member)
            .content(sharedPray.getPray().getContent())
            .deadline(sharedPray.getPray().getDeadline())
            .originPrayId(sharedPray.getPray().getId())
            .category(category)
            .prayType(PrayType.SHARED)
            .build();
        prayRepository.save(pray);
        sharedPrayRepository.deleteById(sharedPrayId);
    }

    @Transactional
    public void deleteSharedPray(String username, SharedPrayDeleteRequestDto sharedPrayDeleteRequestDto) {

        Member member = memberRepository.getMemberByUserId(username);
        List<Long> sharedPrayIds = sharedPrayDeleteRequestDto.getSharedPrayIds();

        for (Long id : sharedPrayIds) {
            delete(member, id);
        }
    }

    private void delete(Member member, Long sharedPrayId) {

        SharedPray sharedPray = sharedPrayRepository.getSharedPrayById(sharedPrayId);

        if (!sharedPray.getMember().equals(member)) {
            throw new CustomException(ErrorStatus.DELETE_NOT_AUTHORIZED_EXCEPTION, ErrorStatus.DELETE_NOT_AUTHORIZED_EXCEPTION.getMessage());
        }
        sharedPrayRepository.deleteById(sharedPrayId);
    }
}
