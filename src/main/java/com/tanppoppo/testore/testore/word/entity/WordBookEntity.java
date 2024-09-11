package com.tanppoppo.testore.testore.word.entity;

import com.tanppoppo.testore.testore.member.entity.MemberEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "wordbook")
public class WordBookEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "wordbook_id")
    private Integer wordbookId;

    @NotNull
    @Size(max = 30)
    @Column(name = "title", nullable = false, length = 30)
    private String title;

    @Size(max = 100)
    @Column(name = "content", length = 100)
    private String content;

    @Size(max = 10)
    @Column(name = "language", length = 10)
    private String language;

    @Column(name = "language_level", columnDefinition = "TINYINT DEFAULT 0")
    private Integer languageLevel;

    @Column(name = "membership_level", columnDefinition = "TINYINT DEFAULT 0")
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

    @Column(name = "created_date", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdDate;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

}
