package com.tanppoppo.testore.testore.exam.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 시험지 정보 데이터 전송 객체
 * @version 0.1.0
 * @since 0.1.0
 * @author KIMGEON64
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamPaperDTO {

    private Integer examPaperId;
    private String title;
    private String content;
    private Integer timeLimit;
    private Integer passScore;
    private String language;
    private Integer languageLevel;
    private Integer membershipLevel;
    private Integer creatorId;
    private Integer ownerId;
    private String imagePath;
    private Boolean publicOption;
    private LocalDateTime studiedDate;
    private LocalDateTime createdDate;
    private LocalDateTime updateDate;

}
