package com.tanppoppo.testore.testore.exam.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @Column(name = "question_type")
    private String questionType;

    @Column(name = "question_score")
    private Integer questionScore;

    @Column(name = "question_order")
    private Integer questionOrder;

}
