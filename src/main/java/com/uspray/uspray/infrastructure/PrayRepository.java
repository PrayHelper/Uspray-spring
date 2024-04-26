package com.uspray.uspray.infrastructure;

import com.uspray.uspray.domain.Member;
import com.uspray.uspray.domain.Pray;
import com.uspray.uspray.exception.ErrorStatus;
import com.uspray.uspray.exception.model.NotFoundException;
import com.uspray.uspray.infrastructure.querydsl.pray.PrayRepositoryCustom;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrayRepository extends JpaRepository<Pray, Long>, PrayRepositoryCustom {

    default Pray getPrayById(Long id) {
        return findById(id).orElseThrow(
            () -> new NotFoundException(ErrorStatus.PRAY_NOT_FOUND_EXCEPTION));
    }

    @EntityGraph(attributePaths = {"groupPray"})
    default Pray getPrayByIdAndMemberId(Long prayId, String username) throws NotFoundException {
        return findById(prayId)
            .filter(pray -> pray.getMember().getUserId().equals(username))
            .orElseThrow(() -> new NotFoundException(
                ErrorStatus.PRAY_NOT_FOUND_EXCEPTION
            ));
    }

    @EntityGraph(attributePaths = {"member"})
    List<Pray> findAllByOriginPrayIdIn(List<Long> prayIds);

    List<Pray> findAllByIdIn(List<Long> prayIds);

    List<Pray> findAllByDeadlineBefore(LocalDate date);

    Pray getPrayByOriginPrayId(Long prayId);

    List<Pray> findByCategoryId(Long categoryId);

    boolean existsByMemberAndOriginPrayId(Member member, Long originPrayId);

    default Pray cancelPray(Long prayId, String username) {
        Pray pray = getPrayByIdAndMemberId(prayId, username);
        if (!Objects.equals(pray.getLastPrayedAt(), LocalDate.now())) {
            throw new NotFoundException(ErrorStatus.ALREADY_CANCEL_EXCEPTION);
        }
        pray.deleteLastPrayedAt();
        return pray;
    }
}
