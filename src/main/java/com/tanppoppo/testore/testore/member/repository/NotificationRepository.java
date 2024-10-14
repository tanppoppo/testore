package com.tanppoppo.testore.testore.member.repository;

import com.tanppoppo.testore.testore.member.entity.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, Integer> {

    // 특정 회원의 최신 10개의 알림을 가져오는 메서드
    List<NotificationEntity> findTop10ByRecipientId_MemberIdOrderByNotificationIdDesc(Integer memberId);

}
