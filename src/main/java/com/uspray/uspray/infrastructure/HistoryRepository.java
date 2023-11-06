package com.uspray.uspray.infrastructure;

import com.uspray.uspray.domain.History;

import java.time.LocalDate;
import java.util.List;

import com.uspray.uspray.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HistoryRepository extends JpaRepository<History, Long> {

//    List<History> getAllByUserIdOrderByDeadlineDesc(String userId);

    List<History> findByMemberOrderByDeadlineDesc(Member member);

    List<History> findByMemberAndContentContaining(Member member, String content);

    List<History> findByMemberAndCreatedAtBetween(Member member, LocalDate start, LocalDate end);

    List<History> findByMemberAndDeadlineBetween(Member member, LocalDate start, LocalDate end);

    List<History> findByMemberAndContentContainingAndCreatedAtBetween(Member member, String content, LocalDate start, LocalDate end);

    List<History> findByMemberAndContentContainingAndDeadlineBetween(Member member, String content, LocalDate start, LocalDate end);
}
