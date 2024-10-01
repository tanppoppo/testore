package com.tanppoppo.testore.testore.exam.entity;

import com.tanppoppo.testore.testore.member.entity.MemberEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 시험지 정보를 저장하는 엔티티 클래스
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

    @Column(name = "time_limit")
    private Integer timeLimit;

    @Min(0)
    @Max(100)
    @Column(name = "pass_score")
    private Integer passScore;

    @Size(max = 10)
    @Column(name = "language", length = 10)
    private String language;

    @Column(name = "language_level", columnDefinition = "TINYINT default 0")
    private Byte languageLevel;

    @Column(name = "membership_level", columnDefinition = "TINYINT default 0")
    private Byte membershipLevel;

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

    @CreatedDate
    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @Column(name = "updated_date")
    private LocalDateTime updateDate;

    @PrePersist
    public void prePersist() {

        if(languageLevel == null){
            languageLevel = 0;
        }
        if(membershipLevel == null){
            membershipLevel = 0;
        }
        if(publicOption == null){
            publicOption = false;
        }
    }
}
