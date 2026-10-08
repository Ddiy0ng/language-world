package com.languageworld.be.domain.term.entity;

import com.languageworld.be.domain.user.entity.User;
import com.languageworld.be.global.auth.dto.ServiceSignupRequestDto;
import com.languageworld.be.global.baseEntity.ChangeableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Table(name = "agreed_terms")
@Entity
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class AgreedTerm extends ChangeableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "is_agreed", nullable = false)
    private boolean isAgreed;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name= "term_id", nullable = false)
    private Term term;

    public static AgreedTerm of(Boolean isTermAgreed, User user, Term term) {

        AgreedTerm agreedTerm = AgreedTerm.builder()
                .isAgreed(isTermAgreed)
                .user(user)
                .term(term)
                .build();

        return agreedTerm;
    }
}
