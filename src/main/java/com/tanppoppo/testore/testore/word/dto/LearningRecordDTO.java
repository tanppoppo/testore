package com.tanppoppo.testore.testore.word.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 학습기록 데이터 전송 객체
 * @version 0.1.0
 * @since 0.1.0
 * @author MinCheolHa
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LearningRecordDTO {

    private Integer recordId;
    private Integer memberId;
    private Integer wordBookId;
    private LocalDateTime createdDate;

}
