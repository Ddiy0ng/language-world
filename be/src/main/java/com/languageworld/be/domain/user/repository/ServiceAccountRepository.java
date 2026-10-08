package com.languageworld.be.domain.user.repository;

import com.languageworld.be.domain.user.entity.ServiceAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ServiceAccountRepository extends JpaRepository<ServiceAccount, Long> {
    
    boolean existsByEmail(String email);

    Optional<ServiceAccount> findByEmail(String email);
}
