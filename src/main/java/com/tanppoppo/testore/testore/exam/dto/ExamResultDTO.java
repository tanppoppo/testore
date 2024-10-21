package com.tanppoppo.testore.testore.exam.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 시험 결과 정보 데이터 전송 객체
 * @version 0.1.0
 * @since 0.1.0
 * @author KIMGEON64
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamResultDTO {

    private Integer examResultId;
    private Integer memberId;
    private Integer examPaperId;
    private Integer examScore;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
    private LocalDateTime createdDate;

    private String examPaperTitle;
    private String examPaperContent;
    private String examPaperImagePath;
    private Integer examQuestionCount;
    private Integer examPaperPassScore;
    private String timeTaken ;
    private Boolean passStatus;

    private String nickname;
    private Integer passScore;

}