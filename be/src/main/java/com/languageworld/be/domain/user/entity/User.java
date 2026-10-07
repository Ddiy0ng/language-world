package com.languageworld.be.domain.user.entity;

import com.languageworld.be.global.auth.dto.ServiceSignupRequestDto;
import com.languageworld.be.global.auth.enumGroup.UserRoleCode;
import com.languageworld.be.domain.language.entity.Language;
import com.languageworld.be.domain.language.entity.Level;
import com.languageworld.be.domain.user.enumGroup.NationCode;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Table(name = "users")
@Entity
@Builder
@Getter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, updatable = false)
    private UUID uuid;

    @Column(name = "user_role", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRoleCode userRole;

    @Column(length = 50)
    private String name;

    private LocalDate birth;

    @Column(name = "age_group")
    @Min(10)
    @Max(90)
    private Integer ageGroup;   // 탈퇴 시 birth 기준 계산하여 삽입

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

    @OneToOne(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private ServiceAccount serviceAccount;

    @OneToOne(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private SocialAccount socialAccount;

    @Column(name = "created_at", nullable = false)
     private LocalDateTime createdAt;

    @Column(name = "deleted_at")
     private LocalDateTime deletedAt;

    public static User of(ServiceSignupRequestDto serviceSignupRequestDto,
                          Language motherTongue,
                          Language learningLanguage,
                          Level learningLanguageLevel) {

        User user = User.builder()
                .uuid(UUID.randomUUID())
                .userRole(UserRoleCode.USER)
                .name(serviceSignupRequestDto.name())
                .birth(serviceSignupRequestDto.birth())
                .nation(serviceSignupRequestDto.nation())
                .motherTongue(motherTongue)
                .learningLanguage(learningLanguage)
                .learningLanguageLevel(learningLanguageLevel)
                .build();

        return user;
    }
}
