package com.tanppoppo.testore.testore.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 리뷰 정보 데이터 전송 객체
 * @version 0.1.0
 * @since 0.1.0
 * @author gyahury
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDTO {

    private Integer reviewId;
    private Integer memberId;
    private Integer itemId;
    private String itemType;
    private String content;
    private Byte rating;
    private LocalDateTime createdDate;
    private LocalDateTime updateDate;
    private String nickname;

}
