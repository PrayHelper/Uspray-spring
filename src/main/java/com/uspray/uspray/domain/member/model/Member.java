package com.uspray.uspray.domain.member.model;

import com.uspray.uspray.domain.member.dto.request.NotificationAgreeDto;
import com.uspray.uspray.domain.member.dto.response.NotificationInfoDto;
import com.uspray.uspray.global.enums.Authority;
import com.uspray.uspray.global.common.model.AuditingTimeEntity;
import com.uspray.uspray.domain.group.model.GroupMember;
import com.uspray.uspray.domain.group.model.GroupPray;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
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
    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private final List<GroupMember> groupMemberList = new ArrayList<>();
    @OneToMany(mappedBy = "author", cascade = CascadeType.REMOVE)
    private final List<GroupPray> groupPrayList = new ArrayList<>();
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
    private Boolean firstNotiAgree = true; // 기도시간 알림
    private Boolean secondNotiAgree = true; // 다른 사람이 내 기도제목을 기도 했을 때
    private Boolean thirdNotiAgree = true; // 다른 사람이 내 기도제목을 공유 받았을 때
    private String socialId;
    @Enumerated(EnumType.STRING)
    private Authority authority;

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

    public NotificationInfoDto getNotificationSetting() {
        return NotificationInfoDto.builder()
            .firstNotiAgree(this.firstNotiAgree)
            .secondNotiAgree(this.secondNotiAgree)
            .thirdNotiAgree(this.thirdNotiAgree)
            .build();
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

    public void updateFcmToken(String fcmToken) {
        this.firebaseToken = fcmToken;
    }
}
