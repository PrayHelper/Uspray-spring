package com.uspray.uspray.infrastructure;

import com.uspray.uspray.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByUserId(String userId);
    Optional<Member> findByPhoneNum(String phoneNum);
    boolean existsByUserId(String userId);
    boolean existsByPhoneNum(String phoneNum);
    Member getMemberByName(String name);
}