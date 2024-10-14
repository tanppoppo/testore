package com.tanppoppo.testore.testore.member.entity;

import com.tanppoppo.testore.testore.common.util.NotificationTypeEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "notification")
public class NotificationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Integer notificationId;  // 알림 고유 ID

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", nullable = false, referencedColumnName = "member_id")
    private MemberEntity recipientId;  // 알림 수신자 (회원)

    @NotNull
    @Column(name = "sender_id", nullable = false)
    private Integer senderId;  // 알림 발신자 ID

    @NotNull
    @Column(name = "notification_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private NotificationTypeEnum notificationType;  // 알림 유형

    @Column(name = "is_read")
    private Boolean isRead;  // 알림이 읽혔는지 여부

    @PrePersist
    public void prePersist() {
        if(isRead == null){
            isRead = false;
        }
    }

}
