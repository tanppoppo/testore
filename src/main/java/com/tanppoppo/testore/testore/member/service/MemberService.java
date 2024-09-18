package com.tanppoppo.testore.testore.member.service;

import com.tanppoppo.testore.testore.member.dto.MemberDTO;
import com.tanppoppo.testore.testore.member.entity.MemberEntity;

public interface MemberService {

    void joinMember(MemberDTO memberDTO);
    boolean verifyEmail(String token);
    void resendVerificationEmail(String email);
    MemberEntity findByEmailVerificationToken(String token);
    String generateEmailVerificationToken(MemberEntity member);
    void sendEmailVerification(String email, String newToken);

}
