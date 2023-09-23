package com.uspray.uspray.infrastructure;

import com.uspray.uspray.domain.Member;
import com.uspray.uspray.exception.ErrorStatus;
import com.uspray.uspray.exception.model.NotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByUserId(String userId);
    boolean existsByUserId(String userId);
    Member findByNameAndPhone(String name, String phone);
    Member findByNameAndPhoneAndUserId(String name, String phone, String userId);

    boolean existsByPhoneNum(String phoneNum);

    default Member getMemberByUserId(String userId) {
        return this.findByUserId(userId).orElseThrow(
            () -> new NotFoundException(ErrorStatus.NOT_FOUND_USER_EXCEPTION,
                ErrorStatus.NOT_FOUND_USER_EXCEPTION.getMessage()));
    }

}
