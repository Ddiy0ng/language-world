package com.languageworld.be.domain.user.entity;

import com.languageworld.be.domain.game.entity.GameResult;
import com.languageworld.be.domain.post.entity.Like;
import com.languageworld.be.domain.post.entity.Post;
import com.languageworld.be.domain.term.entity.AgreedTerm;
import com.languageworld.be.domain.user.dto.request.ChangeUserInfoRequestDto;
import com.languageworld.be.domain.user.dto.request.ChangeUserLanguageInfoRequestDto;
import com.languageworld.be.global.auth.dto.ServiceSignupRequestDto;
import com.languageworld.be.global.auth.dto.social.SocialSignupRequestDto;
import com.languageworld.be.global.auth.enumGroup.UserRoleCode;
import com.languageworld.be.domain.language.entity.Language;
import com.languageworld.be.domain.language.entity.Level;
import com.languageworld.be.domain.user.enumGroup.NationCode;
import com.languageworld.be.global.baseEntity.ChangeableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Builder
@Getter
public class User extends ChangeableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, updatable = false, nullable = false)
    private UUID uuid;  //

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

    @OneToMany(
            mappedBy = "user",
            cascade = CascadeType.REMOVE,
            orphanRemoval = true
    )
    private List<Post> postList = new ArrayList<>();

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

    public static User of(SocialSignupRequestDto socialSignupRequestDto,
                          String profileImageUrl,
                          Language motherTongue,
                          Language learningLanguage,
                          Level learningLanguageLevel) {

        User user = User.builder()
                .uuid(UUID.randomUUID())
                .userRole(UserRoleCode.USER)
                .name(socialSignupRequestDto.name())
                .birth(socialSignupRequestDto.birth())
                .nation(socialSignupRequestDto.nation())
                .profileImageUrl(profileImageUrl)
                .motherTongue(motherTongue)
                .learningLanguage(learningLanguage)
                .learningLanguageLevel(learningLanguageLevel)
                .build();

        return user;

    }

     public void softDeleteUser(Integer ageGroup) {

        // name, birth, profileImageUrl, bio 제거
        this.name = null;
        this.birth = null;
        this.profileImageUrl = null;
        this.bio = null;

         // ageGroup 계산
         this.ageGroup = ageGroup;

         // deletedAt 할당
         this.deletedAt = LocalDateTime.now();
     }

    public void changeUserRole(UserRoleCode userRoleCode) {
        userRole = userRoleCode;
    }

    public void changeUserName(String name) {this.name = name;}

    public void changeUserBio(String bio) {this.bio = bio;}

    public void changeUserNation(NationCode nation) {this.nation = nation;}

    public void changeUserMotherTongue(Language motherTongue) {this.motherTongue = motherTongue;}

    public void changeUserProfileImageUrl(String profileImageUrl) {this.profileImageUrl = profileImageUrl;}

    public void changeUserLearningLanguageAndLevel(Language learningLanguage, Level learningLanguageLevel) {

        this.learningLanguage = learningLanguage;
        this.learningLanguageLevel = learningLanguageLevel;
    }
}
