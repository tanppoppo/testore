package com.tanppoppo.testore.testore.member.repository;

import com.tanppoppo.testore.testore.member.entity.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, Integer> {

    List<NotificationEntity> findByRecipientId_MemberIdOrderByNotificationIdDesc(Integer memberId);
    @Modifying
    @Query(value = "DELETE FROM notification WHERE notification_id NOT IN (SELECT notification_id FROM (SELECT notification_id FROM notification WHERE recipient_id = :memberId ORDER BY notification_id DESC LIMIT 20) AS subquery) AND recipient_id = :memberId", nativeQuery = true)
    void deleteOldNotifications(Integer memberId);

    @Query("SELECT CASE WHEN EXISTS (SELECT 1 FROM NotificationEntity n WHERE n.recipientId.memberId = :memberId AND n.isRead = false) THEN true ELSE false END")
    boolean existsAnyUnreadNotifications(@Param("memberId") Integer memberId);

}
