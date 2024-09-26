package com.tanppoppo.testore.testore.exam.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 시험 문제 정보 데이터 전송 객체
 * @version 0.1.0
 * @since 0.1.0
 * @author KIMGEON64
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamQuestionDTO {

    private Integer examQuestionId;
    private Integer examPaperId;
    private String questionType;
    private Integer questionScore;
    private Integer questionOrder;

}
