package com.uspray.uspray.domain.pray.repository;

import com.uspray.uspray.domain.member.model.Member;
import com.uspray.uspray.domain.pray.model.Pray;
import com.uspray.uspray.domain.pray.model.SharedPray;
import com.uspray.uspray.global.exception.ErrorStatus;
import com.uspray.uspray.global.exception.model.NotFoundException;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SharedPrayRepository extends JpaRepository<SharedPray, Long> {
    
    // 수신자 기준 모두 찾기 (보관함 조회)
    @EntityGraph(attributePaths = {"member"})
    List<SharedPray> findAllByMemberOrderByCreatedAtDesc(Member member);

    @EntityGraph(attributePaths = {"pray"})
    List<SharedPray> findAllByPray(Pray pray);

    boolean existsByMemberAndPray(Member member, Pray pray);
    
    List<SharedPray> findAllByCreatedAtBefore(LocalDate threshold);

    default SharedPray getSharedPrayById(Long id) {
        return this.findById(id).orElseThrow(
            () -> new NotFoundException(ErrorStatus.NOT_FOUND_SHARED_PRAY_EXCEPTION));
    }
}
