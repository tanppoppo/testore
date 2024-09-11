package com.tanppoppo.testore.testore.word.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "word")
public class WordEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "word_id")
    private Integer wordId;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "wordbook_id", nullable = false, referencedColumnName = "wordbook_id")
    private WordBookEntity wordbookId;

    @NotNull
    @Size(max = 30)
    @Column(name = "text_1", nullable = false, length = 30)
    private String text1;

    @Size(max = 30)
    @Column(name = "text_2", length = 30)
    private String text2;

    @Size(max = 30)
    @Column(name = "text_3", length = 30)
    private String text3;

    @Size(max = 30)
    @Column(name = "notes", length = 30)
    private String notes;

    @NotNull
    @Column(name = "checked", nullable = false, columnDefinition = "BOOLEAN default false")
    private Boolean checked;

}
