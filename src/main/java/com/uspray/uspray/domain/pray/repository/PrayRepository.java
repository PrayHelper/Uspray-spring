package com.uspray.uspray.domain.pray.repository;

import com.uspray.uspray.domain.category.model.Category;
import com.uspray.uspray.domain.member.model.Member;
import com.uspray.uspray.domain.pray.model.Pray;
import com.uspray.uspray.global.exception.ErrorStatus;
import com.uspray.uspray.global.exception.model.NotFoundException;
import com.uspray.uspray.domain.pray.repository.querydsl.PrayRepositoryCustom;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query("SELECT p FROM Pray p WHERE p.deadline <= :date")
    List<Pray> findAllByDeadlineBefore(@Param("date") LocalDate date);

    Pray getPrayByOriginPrayId(Long prayId);

    List<Pray> findAllByCategory(Category category);

    List<Pray> findAllByMemberAndCategoryOrderByCreatedAtAsc(Member member, Category category);

    boolean existsByMemberAndOriginPrayId(Member member, Long originPrayId);

    default Pray cancelPray(Long prayId, String username) {
        Pray pray = getPrayByIdAndMemberId(prayId, username);
        if (!Objects.equals(pray.getLastPrayedAt(), LocalDate.now())) {
            throw new NotFoundException(ErrorStatus.ALREADY_CANCEL_EXCEPTION);
        }
        pray.deleteLastPrayedAt();
        return pray;
    }

    @Query(value = "SELECT * FROM Pray p where (p.pray_id = :id)", nativeQuery = true)
    Pray getPrayByIdIgnoreDelete(@Param("id") Long id);

    @Query("SELECT p.count FROM Pray p WHERE p.id = :id")
    Integer getCountById(@Param("id") Long id);
}
