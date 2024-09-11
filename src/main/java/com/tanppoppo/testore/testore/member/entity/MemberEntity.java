package com.tanppoppo.testore.testore.member.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "member")
public class MemberEntity {
    @Id
    @Column(name = "member_id", nullable = false, length = 20)
    private Integer memberId;

    @NotNull
    @Size(max = 100)
    @Column(name = "email", unique = true, nullable = false, length = 100)
    private String email;

    @NotNull
    @Size(max = 50)
    @Column(name = "nickname", nullable = false, length = 50)
    private String nickname;

    @NotNull
    @Size(max = 100)
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

    @Column(name = "status", nullable = false, columnDefinition = "BOOLEAN DEFAULT true")
    private Boolean status;

    @Column(name = "created_date", columnDefinition = "timestamp default current_timestamp")
    private LocalDateTime createdDate;

    @Column(name = "updated_date")
    private LocalDateTime updateDate;

    @Column(name = "last_login_date")
    private LocalDateTime lastLoginDate;

    @Size(max = 100)
    @Column(name = "country", length = 100)
    private String country;

    @Column(name = "image_path")
    private String imagePath;

    @Column(name = "membership_level", nullable = false, columnDefinition = "TINYINT DEFAULT 0")
    private Integer membershipLevel;

    @Size(max = 10)
    @Column(name = "language", length = 10)
    private String language;

    @Column(name = "language_level")
    private Integer languageLevel;

    @Column(name = "notification_option", nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean notificationOption;

    @Column(name = "marketing_option", nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
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
}