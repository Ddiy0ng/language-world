package com.languageworld.be.domain.user.entity;

import com.languageworld.be.domain.auth.enumGroup.SignupTypeCode;
import com.languageworld.be.domain.auth.enumGroup.UserRoleCode;
import com.languageworld.be.domain.language.entity.Language;
import com.languageworld.be.domain.language.entity.Level;
import com.languageworld.be.domain.user.enumGroup.NationCode;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Table(name = "users")
@Entity
@Getter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "signup_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private SignupTypeCode signupType;

    @Column(name = "user_role", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRoleCode userRole;

    private String email;

    private String password;

    @Column(length = 50)
    private String name;

    private LocalDate birth;

    @Column(name = "age_group", nullable = false)
    @Min(10)
    @Max(90)
    private Integer ageGroup;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private NationCode nation;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    private String bio;

    @ManyToOne
    @JoinColumn(name = "mother_tongue_id", nullable = false)
    private Language motherTongue;

    @ManyToOne
    @JoinColumn(name = "learning_language_id", nullable = false)
    private Language learningLanguage;

    @ManyToOne
    @JoinColumn(name = "learning_language_level_id", nullable = false)
    private Level learningLanguageLevel;

    @Column(name = "created_at", nullable = false)
     private LocalDateTime createdAt;

    @Column(name = "deleted_at")
     private LocalDateTime deletedAt;
}
