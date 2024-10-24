package com.tanppoppo.testore.testore.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 북마크 정보 데이터 전송 객체
 * @version 0.1.0
 * @since 0.1.0
 * @author gyahury
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookmarkDTO {

    private Integer bookmarkId;
    private Integer memberId;
    private Integer itemId;
    private String itemType;
    private LocalDateTime createdDate;

}
