package com.languageworld.be.domain.user.repository;

import com.languageworld.be.domain.user.entity.BlockedUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BlockedUserRepository extends JpaRepository<BlockedUser, Long> {

    @Query(
            """
                SELECT CASE WHEN COUNT(blockedUser) > 0 THEN true ELSE false END
                FROM BlockedUser blockedUser
                WHERE (
                    blockedUser.user.uuid = :userUuid AND blockedUser.blockedUser.uuid = :blockUuid
                )
            """
    )
    boolean existsByUser_UuidAndBlockedUser_Uuid(UUID userUuid, UUID blockUuid);

    List<BlockedUser> findAllByUser_Uuid(UUID uuid);
}
