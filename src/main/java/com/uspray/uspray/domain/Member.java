package com.uspray.uspray.domain;

import com.uspray.uspray.Enums.Authority;
import com.uspray.uspray.common.domain.AuditingTimeEntity;

import javax.persistence.*;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE member SET deleted = true WHERE member_id=?")
@Where(clause = "deleted=false")
public class Member extends AuditingTimeEntity {

    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String userId;
    private String password;

    private String name;
    private String phone;
    private String birth;
    private String gender;

    private final Boolean deleted = false;

    @Enumerated(EnumType.STRING)
    private Authority authority;


    public void changePhone(String phone) {
        this.phone = phone;
    }

    public void changePw(String pw) {
        this.password = pw;
    }

    @Builder
    public Member(String userId, String password, String name, String phone, String birth,
        String gender, Authority authority) {
        this.userId = userId;
        this.password = password;
        this.name = name;
        this.phone = phone;
        this.birth = birth;
        this.gender = gender;
        this.authority = authority;
    }
}
