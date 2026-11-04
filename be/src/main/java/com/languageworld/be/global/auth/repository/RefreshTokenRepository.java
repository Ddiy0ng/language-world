package com.languageworld.be.global.auth.repository;

import com.languageworld.be.domain.user.entity.User;
import com.languageworld.be.global.auth.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByTokenId(String id);

    void deleteAllByUser(User user);
}
