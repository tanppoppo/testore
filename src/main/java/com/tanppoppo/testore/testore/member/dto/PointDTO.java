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
public class PointDTO {

    private Integer pointId;
    private Integer memberId;
    private Integer pointChange;
    private String changeReason;
    private LocalDateTime createdDate;

}
