package com.tanppoppo.testore.testore.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberDTO {

    private Integer memberId;
    private String email;
    private String nickname;
    private String memberPassword;
    private String name;
    private String phone;
    private Date birth;
    private String address;
    private Integer gender;
    private Boolean status;
    private LocalDateTime createdDate;
    private LocalDateTime updateDate;
    private LocalDateTime lastLoginDate;
    private String country;
    private String imagePath;
    private Integer membershipLevel;
    private String language;
    private Integer languageLevel;
    private Boolean notificationOption;
    private Boolean marketingOption;
    private String socialLoginProvider;
    private String socialLoginId;
    private String referralCode;
    private String referralId;

}
