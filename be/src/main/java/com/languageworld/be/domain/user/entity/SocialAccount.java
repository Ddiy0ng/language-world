package com.languageworld.be.domain.user.entity;

import com.languageworld.be.global.auth.enumGroup.SocialTypeCode;
import jakarta.persistence.*;
import lombok.*;

@Table(name = "social_accounts")
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class SocialAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private SocialTypeCode signupType;

    @Column(nullable = false)
    private String sub;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public static SocialAccount of(SocialTypeCode socialTypeCode, String sub, User user) {

         SocialAccount socialAccount = SocialAccount.builder()
                 .signupType(socialTypeCode)
                 .sub(sub)
                 .user(user)
                 .build();

         return socialAccount;
    }
}
