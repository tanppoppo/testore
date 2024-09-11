package com.tanppoppo.testore.testore.exam.entity;

import com.tanppoppo.testore.testore.member.entity.MemberEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

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

    @Column(name = "created_date", columnDefinition = "timestamp default current_timestamp")
    private LocalDateTime createdDate;

}