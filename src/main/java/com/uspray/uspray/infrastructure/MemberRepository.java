package com.uspray.uspray.infrastructure;

import com.uspray.uspray.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Transactional
@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Member getMemberById(Long id);
    Member findFirstByOrderById();

    Member getMemberByName(String name);
}
