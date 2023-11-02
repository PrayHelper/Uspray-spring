package com.uspray.uspray.infrastructure.querydsl.clubpray;

import com.uspray.uspray.DTO.clubpray.ClubPrayResponseDto;
import com.uspray.uspray.domain.Club;
import com.uspray.uspray.domain.Member;
import java.util.List;

public interface ClubPrayRepositoryCustom {

    List<ClubPrayResponseDto> getClubPrayList(Club club, Member member);

}
