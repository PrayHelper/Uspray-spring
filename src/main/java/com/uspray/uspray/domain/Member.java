package com.uspray.uspray.domain;

import com.uspray.uspray.DTO.notification.NotificationAgreeDto;
import com.uspray.uspray.Enums.Authority;
import com.uspray.uspray.common.domain.AuditingTimeEntity;

import java.util.ArrayList;
import java.util.List;
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
@SQLDelete(sql = "UPDATE member SET deleted = true WHERE member_id = ?")
@Where(clause = "deleted=false")
public class Member extends AuditingTimeEntity {

    private final Boolean deleted = false;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;
    private String userId;
    private String password;
    private String name;
    private String phone;
    private String birth;
    private String gender;
    private String firebaseToken;
    private Boolean firstNotiAgree = true;
    private Boolean secondNotiAgree = true;
    private Boolean thirdNotiAgree = true;
    private String socialId;
    @Enumerated(EnumType.STRING)
    private Authority authority;

    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<GroupMember> groupMemberList = new ArrayList<>();

    @OneToMany(mappedBy = "author")
    private List<GroupPray> groupPrayList;

    @Builder
    public Member(String userId, String password, String name, String phone, String birth,
        String gender, Authority authority, String socialId) {
        this.userId = userId;
        this.password = password;
        this.name = name;
        this.phone = phone;
        this.birth = birth;
        this.gender = gender;
        this.socialId = socialId;
        this.authority = authority;
    }
  
    public void changeSocialId(String socialId) {
        this.socialId = socialId;
    }

    public void changeFirebaseToken(String firebaseToken) {
        this.firebaseToken = firebaseToken;
    }

    public void changePhone(String phone) {
        this.phone = phone;
    }
    public void changeName(String name) {
        this.name = name;
    }

    public void changeAuthority(Authority authority) {
        this.authority = authority;
    }

    public void changePw(String pw) {
        this.password = pw;
    }

    public void changeNotificationSetting(NotificationAgreeDto notificationAgreeDto) {
        switch (notificationAgreeDto.getNotificationType()) {
            case PRAY_TIME:
                this.firstNotiAgree = notificationAgreeDto.getAgree();
                break;
            case PRAY_FOR_ME:
                this.secondNotiAgree = notificationAgreeDto.getAgree();
                break;
            case SHARED_MY_PRAY:
                this.thirdNotiAgree = notificationAgreeDto.getAgree();
                break;
            default:
                break;
        }
    }
}
