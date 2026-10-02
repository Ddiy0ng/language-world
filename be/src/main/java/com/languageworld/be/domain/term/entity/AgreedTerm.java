package com.languageworld.be.domain.term.entity;

import com.languageworld.be.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import java.time.LocalDateTime;

@Table(name = "agreed_terms")
@Entity
@Getter
public class AgreedTerm {

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

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "modified_at")
    private LocalDateTime modifiedAt;
}
