package com.uspray.uspray.service;

import com.uspray.uspray.DTO.clubpray.ClubPrayRequestDto;
import com.uspray.uspray.domain.Club;
import com.uspray.uspray.domain.ClubPray;
import com.uspray.uspray.domain.Member;
import com.uspray.uspray.infrastructure.ClubPrayRepository;
import com.uspray.uspray.infrastructure.ClubRepository;
import com.uspray.uspray.infrastructure.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClubPrayService {

    private final MemberRepository memberRepository;
    private final ClubRepository clubRepository;
    private final ClubPrayRepository clubPrayRepository;

    @Transactional
    public void createClubPray(ClubPrayRequestDto clubPrayRequestDto, String userId) {
        Member author = memberRepository.getMemberByUserId(userId);
        Club club = clubRepository.getClubById(clubPrayRequestDto.getClubId());
        ClubPray clubPray = ClubPray.builder()
            .club(club)
            .author(author)
            .content(clubPrayRequestDto.getContent())
            .build();
        clubPrayRepository.save(clubPray);
    }

}
