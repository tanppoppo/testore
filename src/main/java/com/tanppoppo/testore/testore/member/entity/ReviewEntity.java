package com.tanppoppo.testore.testore.member.entity;

import com.tanppoppo.testore.testore.common.util.ItemTypeEnum;
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
 * 리뷰 정보를 저장하는 엔티티 클래스
 * @version 0.1.0
 * @since 0.1.0
 * @author gyahury
 */
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "review")
public class ReviewEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id", nullable = false)
    private Integer reviewId;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false, referencedColumnName = "member_id")
    private MemberEntity memberId;

    @NotNull
    @Column(name = "item_id", nullable = false)
    private Integer itemId;

    @NotNull
    @Column(name = "item_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ItemTypeEnum itemType;

    @Column(name = "content")
    private String content;

    @Column(name = "rating", columnDefinition = "TINYINT DEFAULT 3")
    private Byte rating;

    @CreatedDate
    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @PrePersist
    public void prePersist() {
        rating = 3;
    }

}
