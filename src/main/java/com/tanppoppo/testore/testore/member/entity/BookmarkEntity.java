package com.tanppoppo.testore.testore.member.entity;

import jakarta.persistence.*;
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
 * 북마크 정보를 저장하는 엔티티 클래스
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
@Table(name = "bookmark")
public class BookmarkEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bookmark_id", nullable = false)
    private Integer bookmarkId;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false, referencedColumnName = "member_id")
    private MemberEntity memberId;

    @NotNull
    @Column(name = "item_id", nullable = false)
    private Integer itemId;

    @Size(max = 20)
    @Column(name = "item_type", length = 20)
    private String itemType;

    @CreatedDate
    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;

}
