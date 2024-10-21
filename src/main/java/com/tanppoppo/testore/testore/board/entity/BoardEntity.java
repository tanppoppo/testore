package com.tanppoppo.testore.testore.board.entity;

import com.tanppoppo.testore.testore.common.util.BoardTypeEnum;
import com.tanppoppo.testore.testore.member.entity.MemberEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 게시판 엔티티 클래스
 * @author dhkdtjs1541
 * @version 0.1.0
 * @since 0.1.0
 */
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "board")
public class BoardEntity {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "board_id")
        private Integer boardId; // 게시물 ID

        @NotNull
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "member_id", nullable = false)
        private MemberEntity member; // 작성자 (Member 엔티티와의 관계)

        @NotNull
        @Column(name = "board_type", nullable = false)
        @Enumerated(EnumType.STRING)
        private BoardTypeEnum boardType; // 게시물 타입 (enum)

        @NotNull
        @Size(min = 2, max = 50)
        @Column(name= "title", nullable = false, length = 50)
        private String title; // 제목

        @NotNull
        @Size(min = 2, max = 300)
        @Column(name = "content", nullable = false, length = 300)
        private String content; // 내용

        @Column(name = "view_count")
        private Integer viewCount; // 조회수

        @NotNull
        @CreatedDate
        @Column(name = "created_date", nullable = false, updatable = false)
        private LocalDateTime createdDate; // 작성일

        @LastModifiedDate
        @Column(name = "updated_date")
        private LocalDateTime updateDate;

        @OneToMany(fetch = FetchType.LAZY, mappedBy = "board", cascade = CascadeType.REMOVE)
        private List<CommentEntity> comments;

        @PrePersist
        public void prePersist() {

                if(viewCount == null) {
                        viewCount = 0;
                }

        }

    }
