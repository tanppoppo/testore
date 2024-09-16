package com.tanppoppo.testore.testore.exam.entity;

import com.tanppoppo.testore.testore.member.entity.MemberEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 시험 결과를 저장하는 엔티티 클래스
 * @version 0.1.0
 * @since 0.1.0
 * @author KIMGEON64
 */
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "exam_result")
public class ExamResultEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exam_result_id")
    private Integer examResultId;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false, referencedColumnName = "member_id")
    private MemberEntity memberId;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_paper_id", nullable = false, referencedColumnName = "exam_paper_id")
    private ExamPaperEntity examPaperId;

    @NotNull
    @Column(name = "exam_score", nullable = false)
    private Integer examScore;

    @NotNull
    @Column(name = "exam_duration", nullable = false)
    private Integer examDuration;

    @CreatedDate
    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;

}