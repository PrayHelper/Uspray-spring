package com.uspray.uspray.infrastructure;

import com.uspray.uspray.DTO.pray.PrayDto;
import com.uspray.uspray.domain.Member;
import com.uspray.uspray.domain.SharedPray;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SharedPrayRepository extends JpaRepository<SharedPray, Long> {

    // 수신자 기준 모두 찾기 (보관함 조회)
    @EntityGraph(attributePaths = {"member"})
    List<SharedPray> findAllByMemberOrderByCreatedAtDesc(Member member);

    List<SharedPray> findAllByCreatedAtBefore(LocalDate threshold);
}
