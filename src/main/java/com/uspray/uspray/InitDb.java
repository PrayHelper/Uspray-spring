package com.uspray.uspray;

import com.uspray.uspray.Enums.Authority;
import com.uspray.uspray.domain.Category;
import com.uspray.uspray.domain.Member;
import com.uspray.uspray.domain.Pray;
import java.time.LocalDate;
import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class InitDb {

  private final InitService initService;

  @PostConstruct
  public void init() {
    initService.dbInit();
  }

  @Component
  @Transactional
  @RequiredArgsConstructor
  static class InitService {

    private final EntityManager em;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    @RequiredArgsConstructor
    static class InitService {

        private final EntityManager em;
        private final PasswordEncoder passwordEncoder;

        @Transactional
        public void dbInit() {
          Member member = Member.builder()
              .userId("test")
              .password(passwordEncoder.encode("test"))
              .name("홍길동")
              .phone("01012345678")
              .birth("2002-02-01")
              .authority(Authority.ROLE_USER)
              .gender("female")
              .build();
          em.persist(member);

          Category category = Category.builder()
              .name("기타 카테고리")
              .color("#FFFFFF")
              .member(member)
              .build();
          em.persist(category);

          Pray pray = Pray.builder()
              .content("테스트 기도")
              .deadline(LocalDate.parse("2025-01-01"))
              .member(member)
              .category(category)
              .prayType(com.uspray.uspray.Enums.PrayType.PERSONAL)
              .build();
          em.persist(pray);
        }

  }
}
