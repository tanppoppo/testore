package com.tanppoppo.testore.testore.member.service;

import com.tanppoppo.testore.testore.member.dto.MemberDTO;
import com.tanppoppo.testore.testore.member.entity.MemberEntity;
import com.tanppoppo.testore.testore.security.AuthenticatedUser;

public interface MemberService {

    void joinMember(MemberDTO memberDTO);
    boolean verifyEmail(String token);
    void resendVerificationEmail(String email);
    MemberEntity findByEmailVerificationToken(String token);
    String generateEmailVerificationToken(MemberEntity member);
    void sendEmailVerification(String email, String newToken);
    int getPointSumByMemberId(int memberId);
    void createAndDeleteBookmarkByMemberId(Integer examPaperId, AuthenticatedUser user);
    void createAndDeleteItemLikeByMemberId(Integer examPaperId, AuthenticatedUser user);
}
