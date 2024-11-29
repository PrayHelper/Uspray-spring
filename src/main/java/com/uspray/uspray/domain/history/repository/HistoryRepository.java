package com.uspray.uspray.domain.history.repository;

import com.uspray.uspray.domain.history.model.History;
import com.uspray.uspray.domain.member.model.Member;
import com.uspray.uspray.global.exception.ErrorStatus;
import com.uspray.uspray.global.exception.model.NotFoundException;
import com.uspray.uspray.domain.history.repository.querydsl.HistoryRepositoryCustom;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HistoryRepository extends JpaRepository<History, Long>, HistoryRepositoryCustom {

    Page<History> findByMemberAndOriginPrayIdIsNull(Member member, Pageable pageable);

    Page<History> findByMemberAndOriginPrayIdIsNotNull(Member member, Pageable pageable);

    Optional<History> findByIdAndMember(Long historyId, Member member);

    Optional<History> findById(Long historyId);
}
