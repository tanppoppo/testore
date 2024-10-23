package com.tanppoppo.testore.testore.word.entity;

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

/**
 * 단어 정보를 저장하는 엔티티 클래스
 * @version 0.1.0
 * @since 0.1.0
 * @author MinCheolHa
 */
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "word")
public class WordEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "word_id")
    private Integer wordId;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "wordbook_id", nullable = false, referencedColumnName = "wordbook_id")
    private WordBookEntity wordBookId;

    @Column(name = "word_num")
    private Integer wordNum;

    @NotNull
    @Size(min = 1, max = 10)
    @Column(name = "text_1", nullable = false, length = 10)
    private String text1;

    @NotNull
    @Size(min = 1, max = 30)
    @Column(name = "text_2", nullable = false, length = 30)
    private String text2;

    @NotNull
    @Size(min = 1, max = 10)
    @Column(name = "text_3", nullable = false, length = 10)
    private String text3;

    @Size(max = 30)
    @Column(name = "notes", length = 30)
    private String notes;

    @NotNull
    @Column(name = "checked", nullable = false)
    private Boolean checked;

    @CreatedDate
    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    @PrePersist
    public void prePersist() {

        if(checked == null) {
            checked = false;
        }

    }
}
