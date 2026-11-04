package com.languageworld.be.global.auth.entity;

import com.languageworld.be.domain.user.entity.User;
import io.jsonwebtoken.Claims;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "refresh_tokens")
@Getter
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String tokenId;

    @Column(name = "issued_at")
    private Date issuedAt;

    @Column(name = "expired_at")
    private Date expiredAt;

    @Column(name = "deleted_at")
    private Date deletedAt;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public static RefreshToken of(Claims claims, User user) {

        RefreshToken refreshToken = RefreshToken.builder()
                .tokenId(claims.getId())
                .issuedAt(claims.getIssuedAt())
                .expiredAt(claims.getExpiration())
                .user(user)
                .build();

        return refreshToken;
    }
}
