package com.languageworld.be.domain.user.entity;

import com.languageworld.be.global.auth.enumGroup.SignupTypeCode;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

@Table(name = "social_accounts")
@Entity
@Builder
@Getter
public class SocialAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private SignupTypeCode signupType;

    @Column(name = "user_id", nullable = false)
    private String providerUserId;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
