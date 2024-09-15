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
public class ReviewDTO {

    private Integer reviewId;
    private Integer memberId;
    private Integer itemId;
    private String itemType;
    private String content;
    private Byte rating;
    private LocalDateTime createdDate;

}
