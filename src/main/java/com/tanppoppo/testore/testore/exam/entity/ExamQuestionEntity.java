package com.tanppoppo.testore.testore.exam.entity;

import com.tanppoppo.testore.testore.common.util.QuestionTypeEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 시험 문제 정보를 저장하는 엔티티 클래스
 * @version 0.1.0
 * @since 0.1.0
 * @author KIMGEON64
 */
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "exam_question")
public class ExamQuestionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exam_question_id")
    private Integer examQuestionId;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_paper_id", nullable = false, referencedColumnName = "exam_paper_id")
    private ExamPaperEntity examPaperId;

    @NotNull
    @Column(name = "questionType", nullable = false)
    @Enumerated(EnumType.STRING)
    private QuestionTypeEnum questionType;

    @Column(name = "question_score")
    private Integer questionScore;

    @Column(name = "question_order")
    private Integer questionOrder;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "examQuestionId", cascade = CascadeType.REMOVE)
    private List<QuestionParagraphEntity> questionParagraphs;

}
