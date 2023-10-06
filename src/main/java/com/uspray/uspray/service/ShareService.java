package com.uspray.uspray.service;

import com.uspray.uspray.DTO.sharedpray.response.SharedPrayResponseDto;
import com.uspray.uspray.domain.Member;
import com.uspray.uspray.domain.SharedPray;
import com.uspray.uspray.infrastructure.MemberRepository;
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

    @Transactional(readOnly = true)
    public List<SharedPrayResponseDto> getSharedPrayList(String userId) {
        Member member = memberRepository.getMemberByUserId(userId);
//        List<SharedPray> sharedPrayList = sharedPrayRepository.findAllByMemberId(userId);
        List<SharedPray> sharedPrayList = sharedPrayRepository.findAllByMember(member);
        return sharedPrayList.stream()
            .map(SharedPrayResponseDto::of)
            .collect(Collectors.toList());

    }
}
