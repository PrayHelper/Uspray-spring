package com.uspray.uspray.infrastructure;

import com.uspray.uspray.domain.Member;
import com.uspray.uspray.domain.Pray;
import com.uspray.uspray.domain.SharedPray;
import com.uspray.uspray.exception.ErrorStatus;
import com.uspray.uspray.exception.model.NotFoundException;
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
