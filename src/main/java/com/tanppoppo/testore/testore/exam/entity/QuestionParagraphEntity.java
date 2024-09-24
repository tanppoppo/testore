package com.tanppoppo.testore.testore.exam.entity;

import com.tanppoppo.testore.testore.common.util.ParagraphTypeEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 시험 문제 단락을 저장하는 엔티티 클래스
 * @author gyahury
 * @version 0.1.0
 * @since 0.1.0
 */
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "question_paragraph")
public class QuestionParagraphEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_paragraph_id")
    private Integer questionParagraphId;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_question_id", nullable = false, referencedColumnName = "exam_question_id")
    private ExamQuestionEntity examQuestionId;

    @NotNull
    @Column(name = "paragraphType", nullable = false)
    @Enumerated(EnumType.STRING)
    private ParagraphTypeEnum paragraphType;

    @Column(name = "paragraph_content")
    private String paragraphContent;

    @Column(name = "paragraph_order")
    private Integer paragraphOrder;

    @Column(name = "correct", columnDefinition = "BOOLEAN")
    private Boolean correct;

    @PrePersist
    public void prePersist() {
        if (correct == null) {
            correct = false;
        }
    }
}
