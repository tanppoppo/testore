package com.tanppoppo.testore.testore.word.entity;

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
 * 단어장 정보를 저장하는 엔티티 클래스
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
@Table(name = "wordbook")
public class WordBookEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "wordbook_id")
    private Integer wordBookId;

    @NotNull
    @Size(min = 2, max = 30, message = "단어장 이름은 2자 이상 30자 이하로 입력해 주세요.")
    @Column(name = "title", nullable = false, length = 30)
    private String title;

    @NotNull
    @Size(min = 2, max = 100, message = "단어장 설명은 2자 이상 100자 이하로 입력해 주세요.")
    @Column(name = "content", nullable = false, length = 100)
    private String content;

    @Size(max = 10)
    @Column(name = "language", length = 10)
    private String language;

    @Column(name = "language_level", columnDefinition = "TINYINT DEFAULT 0")
    private Byte languageLevel;

    @Column(name = "membership_level", columnDefinition = "TINYINT DEFAULT 0")
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

    @LastModifiedDate
    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "wordBookId", cascade = CascadeType.REMOVE)
    private List<WordEntity> words;

    @PrePersist
    public void prePersist() {

        if (languageLevel == null){
            languageLevel = 0;
        }
        if (membershipLevel == null){
            membershipLevel = 0;
        }
        if (publicOption == null){
            publicOption = false;
        }
    }
}
