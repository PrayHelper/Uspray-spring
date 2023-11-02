package com.uspray.uspray.service;

import com.uspray.uspray.DTO.clubpray.ClubPrayRequestDto;
import com.uspray.uspray.DTO.clubpray.ClubPrayResponseDto;
import com.uspray.uspray.DTO.clubpray.ClubPrayUpdateDto;
import com.uspray.uspray.domain.Club;
import com.uspray.uspray.domain.ClubPray;
import com.uspray.uspray.domain.Member;
import com.uspray.uspray.infrastructure.ClubPrayRepository;
import com.uspray.uspray.infrastructure.ClubRepository;
import com.uspray.uspray.infrastructure.MemberRepository;
import java.util.List;
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

    @Transactional
    public void updateClubPray(ClubPrayUpdateDto clubPrayUpdateDto) {
        ClubPray clubpray = clubPrayRepository.getClubPrayById(
            clubPrayUpdateDto.getClubPrayId());
        clubpray.changeContent(clubPrayUpdateDto.getContent());
    }

    //clubId와 자신의 Id를 이용해 club pray들 반환 + 작성자인지 and 좋아요를 눌렀는지 확인 가능
    public List<ClubPrayResponseDto> getClubPray(Long clubId, String userId) {
        Member member = memberRepository.getMemberByUserId(userId);
        Club club = clubRepository.getClubById(clubId);

        List<ClubPrayResponseDto> clubPrayList = clubPrayRepository.getClubPrayList(club, member);

        for (ClubPrayResponseDto clubPrayResponseDto : clubPrayList) {
            if (clubPrayResponseDto.getAuthorId().equals(member.getId())) {
                clubPrayResponseDto.changeOwner();
            }
        }

        return clubPrayList;
    }

}
