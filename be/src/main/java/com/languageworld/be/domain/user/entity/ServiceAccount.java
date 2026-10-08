package com.languageworld.be.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;

@Table(name = "service_self_accounts")
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Builder
@Getter
public class ServiceAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public static ServiceAccount of(String email, String encodedPassword, User user) {

        ServiceAccount serviceAccount = ServiceAccount.builder()
                .email(email)
                .password(encodedPassword)
                .user(user)
                .build();

        return serviceAccount;
    }
}
