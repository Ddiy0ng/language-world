package com.languageworld.be.domain.user.repository;

import com.languageworld.be.domain.user.entity.SocialAccount;
import com.languageworld.be.global.auth.enumGroup.SocialTypeCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SocialAccountRepository extends JpaRepository<SocialAccount, Long> {
    Optional<SocialAccount> findBySignupTypeAndSub(SocialTypeCode signupType, String sub);
}
