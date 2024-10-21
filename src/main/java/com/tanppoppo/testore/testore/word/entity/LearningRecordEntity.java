package com.tanppoppo.testore.testore.word.entity;

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
 * 학습 기록을 저장하는 엔티티 클래스
 * @author MinCheolHa
 * @version 0.1.0
 * @since 0.1.0
 */
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "learning_recode")
public class LearningRecordEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "record_id")
    private Integer recordId;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false, referencedColumnName = "member_id")
    private MemberEntity memberId;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "wordbook_id", nullable = false, referencedColumnName = "wordbook_id")
    private WordBookEntity wordBookId;

    @CreatedDate
    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;

}
