package com.tanppoppo.testore.testore.member.service;

import com.tanppoppo.testore.testore.common.util.NotificationTypeEnum;
import com.tanppoppo.testore.testore.member.dto.MemberDTO;
import com.tanppoppo.testore.testore.member.dto.NotificationDTO;
import com.tanppoppo.testore.testore.member.entity.MemberEntity;
import com.tanppoppo.testore.testore.security.AuthenticatedUser;

import java.util.List;

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
    void createAndDeleteWordBookBookmarkByMemberId(Integer wordBookId, AuthenticatedUser user);
    void createAndDeleteWordBookItemLikeByMemberId(Integer wordBookId, AuthenticatedUser user);
    void saveNotification(Integer userId, Integer itemId, NotificationTypeEnum type);
    List<NotificationDTO> getNotificationsAndMarkAsRead(Integer id);
    Boolean getUnreadNotification(Integer userId);

}
