package com.uspray.uspray.domain.member.repository;

import com.uspray.uspray.domain.member.model.Member;
import com.uspray.uspray.global.exception.ErrorStatus;
import com.uspray.uspray.global.exception.model.NotFoundException;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByUserId(String userId);

    boolean existsByUserId(String userId);

    boolean existsByPhone(String phone);
    
    Optional<Member> findBySocialId(String socialId);

    Member findByNameAndPhone(String name, String phone);

    Member findByUserIdAndPhone(String userId, String phone);

    default Member getMemberByUserId(String userId) {
        return this.findByUserId(userId).orElseThrow(
            () -> new NotFoundException(ErrorStatus.NOT_FOUND_USER_EXCEPTION));
    }

    default Member getMemberById(Long id) {
        return this.findById(id).orElseThrow(
            () -> new NotFoundException(ErrorStatus.NOT_FOUND_USER_EXCEPTION));
    }

    @Query("select m.firebaseToken from Member m where m.firstNotiAgree = :agree")
    List<String> getDeviceTokensByFirstNotiAgree(@Param("agree") Boolean agree);
}
