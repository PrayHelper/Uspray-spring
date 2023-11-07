package com.uspray.uspray.infrastructure;

import com.uspray.uspray.domain.History;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.uspray.uspray.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface HistoryRepository extends JpaRepository<History, Long>, HistoryRepositoryCustom {
    Page<History> findByMember(Member member, Pageable pageable);

    @Query("SELECT h FROM History h WHERE h.member = :member AND h.createdAt <= :endDateTime AND h.deadline >= :startDate")
    List<History> findAllByPeriodOverlap(@Param("startDate") LocalDate startDate,
                                         @Param("endDateTime") LocalDateTime endDateTime,
                                         @Param("member") Member member);

    @Query("SELECT h FROM History h WHERE h.member = :member AND h.content LIKE %:keyword% AND h.createdAt <= :endDateTime AND h.deadline >= :startDate")
    List<History> findAllByKeywordAndPeriodOverlap(@Param("keyword") String keyword,
                                                   @Param("startDate") LocalDate startDate,
                                                   @Param("endDateTime") LocalDateTime endDateTime,
                                                   @Param("member") Member member);
}
