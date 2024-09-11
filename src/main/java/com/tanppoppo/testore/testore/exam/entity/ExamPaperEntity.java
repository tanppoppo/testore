package com.tanppoppo.testore.testore.exam.entity;

import com.tanppoppo.testore.testore.member.entity.MemberEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
@Table(name = "exam_paper")
public class ExamPaperEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exam_paper_id")
    private Integer examPaperId;

    @NotNull
    @Size(max = 30)
    @Column(name = "title", nullable = false, length = 30)
    private String title;

    @NotNull
    @Size(max = 100)
    @Column(name = "content", nullable = false, length = 100)
    private String content;

    @NotNull
    @Column(name = "time_limit", nullable = false)
    private Integer timeLimit;

    @NotNull
    @Column(name = "pass_score", nullable = false)
    private Integer passScore;

    @Size(max = 10)
    @Column(name = "language", length = 10)
    private String language;

    @Column(name = "language_level", columnDefinition = "TINYINT default 0")
    private Integer languageLevel;

    @Column(name = "membership_level", columnDefinition = "TINYINT default 0")
    private Integer membershipLevel;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false, referencedColumnName = "member_id")
    private MemberEntity creatorId;

    @NotNull
    @Column(name = "owner_id", nullable = false)
    private Integer ownerId;

    @Column(name = "image_path")
    private String imagePath;

    @Column(name = "public_option", columnDefinition = "BOOLEAN default false")
    private Boolean publicOption;

    @Column(name = "studied_date")
    private LocalDateTime studiedDate;

    @Column(name = "created_date", columnDefinition = "timestamp default current_timestamp")
    private LocalDateTime createdDate;

    @Column(name = "updated_date")
    private LocalDateTime updateDate;

}