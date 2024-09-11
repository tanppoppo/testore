package com.tanppoppo.testore.testore.word.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WordDTO {

    private Integer wordId;
    private Integer wordbookId;
    private String text1;
    private String text2;
    private String text3;
    private String notes;
    private Boolean checked;

}
