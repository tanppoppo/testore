package com.tanppoppo.testore.testore.exam.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "exam_item")
public class ExamItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exam_item_id")
    private Integer examItemId;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_paper_id", nullable = false, referencedColumnName = "exam_paper_id")
    private ExamPaperEntity examPaperId;

    @NotNull
    @Column(name = "question", nullable = false)
    private String question;

    @NotNull
    @Column(name = "answer", columnDefinition = "TINYINT")
    private Integer answer;

    @Column(name = "paragraph_description")
    private String paragraphDescription;

    @Column(name = "paragraph_content")
    private String paragraphContent;

    @NotNull
    @Column(name = "text_1", nullable = false)
    private String text1;

    @Column(name = "text_2")
    private String text2;

    @Column(name = "text_3")
    private String text3;

    @Column(name = "text_4")
    private String text4;

    @Column(name = "text_5")
    private String text5;

    @NotNull
    @Column(name = "item_score", nullable = false)
    private Integer itemScore;

}
