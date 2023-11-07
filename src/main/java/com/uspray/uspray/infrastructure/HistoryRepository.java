package com.uspray.uspray.infrastructure;

import com.uspray.uspray.domain.History;

import com.uspray.uspray.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HistoryRepository extends JpaRepository<History, Long>, HistoryRepositoryCustom {
    Page<History> findByMember(Member member, Pageable pageable);

}
