package com.tanppoppo.testore.testore.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {

    private Integer notificationId;  // 알림 고유 ID
    private Integer senderId;  // 알림 발신자 ID
    private String notificationType;  // 알림 유형
    private Boolean isRead;  // 알림이 읽혔는지 여부
    private Integer recipientId;  // 알림 수신자 ID
    private LocalDateTime createdDate;
    private LocalDateTime updateDate;

    private String senderNickname;
    private String itemTitle;

}
