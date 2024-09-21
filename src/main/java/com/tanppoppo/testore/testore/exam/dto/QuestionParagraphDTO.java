package com.tanppoppo.testore.testore.exam.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 시험 문제 단락 정보 데이터 전송 객체
 * @author gyahury
 * @version 0.1.0
 * @since 0.1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionParagraphDTO {

    private Integer questionParagraphId;
    private Integer examQuestionId;
    private String paragraphType;
    private String paragraphContent;
    private Integer paragraphOrder;
    private Boolean correct;

    private Integer choiceIndex;

}
