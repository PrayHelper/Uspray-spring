package com.uspray.uspray.service;

import com.uspray.uspray.DTO.sharedpray.request.SharedPrayRequestDto;
import com.uspray.uspray.DTO.sharedpray.response.SharedPrayResponseDto;
import com.uspray.uspray.domain.Member;
import com.uspray.uspray.domain.SharedPray;
import com.uspray.uspray.exception.ErrorStatus;
import com.uspray.uspray.exception.model.CustomException;
import com.uspray.uspray.exception.model.NotFoundException;
import com.uspray.uspray.infrastructure.MemberRepository;
import com.uspray.uspray.infrastructure.PrayRepository;
import com.uspray.uspray.infrastructure.SharedPrayRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ShareService {

    private final SharedPrayRepository sharedPrayRepository;
    private final MemberRepository memberRepository;
    private final PrayRepository prayRepository;

    @Transactional(readOnly = true)
    public List<SharedPrayResponseDto> getSharedPrayList(String userId) {
        Member member = memberRepository.getMemberByUserId(userId);
        List<SharedPray> sharedPrayList = sharedPrayRepository.findAllByMember(member);
        return sharedPrayList.stream()
            .map(SharedPrayResponseDto::of)
            .collect(Collectors.toList());

    }

    @Transactional
    public void sharePray(String userId, SharedPrayRequestDto sharedPrayRequestDto) {
        if (sharedPrayRequestDto.getReceiverId().equals(userId)) {
            throw new CustomException(ErrorStatus.SENDER_RECEIVER_SAME_EXCEPTION, ErrorStatus.SENDER_RECEIVER_SAME_EXCEPTION.getMessage());
        }

        if (prayRepository.getPrayById(sharedPrayRequestDto.getPrayId()).getMember() != memberRepository.getMemberByUserId(userId)) {
            throw new CustomException(ErrorStatus.SHARE_NOT_AUTHORIZED_EXCEPTION, ErrorStatus.SHARE_NOT_AUTHORIZED_EXCEPTION.getMessage());
        }
        SharedPray sharedPray = SharedPray.builder()
            .member(memberRepository.findByUserId(sharedPrayRequestDto.getReceiverId()).orElseThrow(() -> new NotFoundException(ErrorStatus.NOT_FOUND_USER_EXCEPTION, ErrorStatus.NOT_FOUND_USER_EXCEPTION.getMessage())))
            .pray(prayRepository.getPrayById(sharedPrayRequestDto.getPrayId()))
            .build();
        sharedPrayRepository.save(sharedPray);
    }
}
