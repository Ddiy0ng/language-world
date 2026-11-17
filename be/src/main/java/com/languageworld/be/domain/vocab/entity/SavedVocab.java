package com.languageworld.be.domain.vocab.entity;

import com.languageworld.be.domain.user.entity.User;
import com.languageworld.be.global.baseEntity.ChangeableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Table(name = "saved_vocabs")
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Getter
public class SavedVocab extends ChangeableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 300)
    private String memo;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "vocab_meaning_id", nullable = false)
    private VocabMeaning vocabMeaning;

    public static SavedVocab of(String memo, User user, VocabMeaning vocabMeaning) {

        return SavedVocab.builder()
                .memo(memo)
                .user(user)
                .vocabMeaning(vocabMeaning)
                .build();
    }

    public void changeMemo(String memo) {this.memo = memo;}
}
