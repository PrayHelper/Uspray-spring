package com.uspray.uspray.domain;

import com.uspray.uspray.common.domain.AuditingTimeEntity;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
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
    private Long id;

    private String userId;
    private String password;

    private String name;
    private String phoneNum;
    private String birth;
    private String gender;

    private final Boolean deleted = false;

    @Builder
    public Member(String userId, String password, String name, String phoneNum, String birth,
        String gender) {
        this.userId = userId;
        this.password = password;
        this.name = name;
        this.phoneNum = phoneNum;
        this.birth = birth;
        this.gender = gender;
    }
}