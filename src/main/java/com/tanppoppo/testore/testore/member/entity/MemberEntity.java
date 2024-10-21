package com.tanppoppo.testore.testore.member.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * 회원 정보를 저장하는 엔티티 클래스
 * @version 0.1.0
 * @since 0.1.0
 * @author gyahury
 */
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "member")
public class MemberEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id", nullable = false)
    private Integer memberId;

    @NotNull
    @Size(min = 2, max = 100) // 문자열 길이 제한
    @Column(name = "email", unique = true, nullable = false, length = 100)
    private String email;

    @NotNull
    @Size(min = 2, max = 50) // 닉네임 유효성 검사
    @Column(name = "nickname", nullable = false, length = 50)
    private String nickname;

    @NotNull
    @Size(min = 8, max = 100) // 비밀번호 유효성 검사
    @Column(name = "member_password", nullable = false, length = 100)
    private String memberPassword;

    @Size(max = 50)
    @Column(name = "name", length = 50)
    private String name;

    @Size(max = 30)
    @Column(name = "phone", length = 30)
    private String phone;

    @Column(name = "birth", columnDefinition = "DATE")
    private Date birth;

    @Size(max = 100)
    @Column(name = "address", length = 100)
    private String address;

    @Column(name = "gender")
    private Integer gender;

    @Column(name = "status", columnDefinition = "BOOLEAN DEFAULT true")
    private Boolean status;

    @CreatedDate
    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(name = "updated_date")
    private LocalDateTime updateDate;

    @Column(name = "last_login_date")
    private LocalDateTime lastLoginDate;

    @Size(max = 100)
    @Column(name = "country", length = 100)
    private String country;

    @Column(name = "image_path")
    private String imagePath;

    @Column(name = "membership_level", columnDefinition = "TINYINT DEFAULT 0")
    private Byte membershipLevel;

    @Size(max = 10)
    @Column(name = "language", length = 10)
    private String language;

    @Column(name = "language_level")
    private Integer languageLevel;

    @Column(name = "notification_option", columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean notificationOption;

    @Column(name = "marketing_option", columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean marketingOption;

    @Size(max = 10)
    @Column(name = "social_login_provider", length = 10)
    private String socialLoginProvider;

    @Size(max = 100)
    @Column(name = "social_login_id", length = 100)
    private String socialLoginId;

    @Size(max = 10)
    @Column(name = "referral_code", length = 10)
    private String referralCode;

    @Size(max = 100)
    @Column(name = "referral_id", length = 100)
    private String referralId;

    @Size(max = 100)
    @Column(name = "email_verification_token", length = 100)
    private String emailVerificationToken;

    @Column(name = "token_expiration_time")
    private LocalDateTime tokenExpirationTime;

    @PrePersist
    public void prePersist() {

        if(status == null) {
            status = false;
        }
        if(membershipLevel == null) {
            membershipLevel = 0;
        }
        if(notificationOption == null) {
            notificationOption = false;
        }
        if(marketingOption == null) {
            marketingOption = false;
        }

    }

}
