package com.tanppoppo.testore.testore.exam.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamItemDTO {

    private Integer examItemId;
    private Integer examPaperId;
    private String question;
    private Integer answer;
    private String paragraphDescription;
    private String paragraphContent;
    private String text1;
    private String text2;
    private String text3;
    private String text4;
    private String text5;
    private Integer itemScore;

}
