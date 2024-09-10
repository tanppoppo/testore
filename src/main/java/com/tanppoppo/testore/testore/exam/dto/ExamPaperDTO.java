package com.tanppoppo.testore.testore.exam.dto;

import com.tanppoppo.testore.testore.member.entity.MemberEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
