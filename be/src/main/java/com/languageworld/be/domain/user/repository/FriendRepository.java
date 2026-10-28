package com.languageworld.be.domain.user.repository;

import com.languageworld.be.domain.user.entity.Friend;
import com.languageworld.be.domain.user.enumGroup.FriendStatusCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Long> {

    @Query(
            """
                SELECT CASE WHEN COUNT(friend) > 0 THEN true ELSE false END
                FROM Friend friend
                WHERE (
                        (friend.user.uuid = :userUuid AND friend.friend.uuid = :friendUuid)
                        OR friend.user.uuid = :friendUuid AND friend.friend.uuid = :userUuid
                )
            """
    )
    boolean existsByUser_UuidAndFriend_Uuid(UUID userUuid, UUID friendUuid);

    @Query(
            """
                SELECT friend
                FROM Friend friend
                JOIN FETCH friend.user
                JOIN FETCH friend.friend
                WHERE friend.friendStatus = :status
                    AND (
                        friend.user.uuid = :uuid
                        OR friend.friend.uuid = :uuid
                    )
                    AND (
                        :keyword IS NULL
                        OR (
                            friend.user.uuid = :uuid
                            AND LOWER(friend.friend.name)
                                LIKE LOWER(CONCAT('%', :keyword, '%'))
                        )
                        OR (
                            friend.friend.uuid = :uuid
                            AND LOWER(friend.user.name)
                                LIKE LOWER(CONCAT('%', :keyword, '%'))
                        )
                    )
                    AND NOT EXISTS (
                        SELECT blockedUser
                        FROM BlockedUser blockedUser
                        WHERE blockedUser.user.uuid = :uuid
                            AND (
                                (friend.user.uuid = :uuid AND blockedUser.blockedUser = friend.friend)
                                OR (friend.friend.uuid = :uuid AND blockedUser.blockedUser = friend.user)
                            )
                    )
            """
    )
    List<Friend> findAllFriendUsers(
            UUID uuid,
            String keyword,
            @Param("status") FriendStatusCode friendStatusCode);

    Optional<Friend> findByIdAndFriend_Uuid(Long requestId, UUID authenticatedUserUuid);

    Optional<Friend> findByIdAndUser_Uuid(Long requestId, UUID authenticatedUserUuid);

    @Query(
        """
            SELECT friend
            FROM Friend friend
            WHERE friend.user.uuid = :uuid
                AND friend.friendStatus IN :statuses
            ORDER BY friend.createdAt DESC
        """
    )
    List<Friend> findAllSentFriendRequest(
            UUID uuid,
            @Param("statuses") List<FriendStatusCode> friendStatusCodes);

    @Query(
            """
                SELECT friend
                FROM Friend friend
                WHERE friend.friend.uuid = :uuid
                    AND friend.friendStatus = :status
                    AND NOT EXISTS (
                        SELECT blockedUser
                        FROM BlockedUser blockedUser
                        WHERE blockedUser.user = friend.friend AND blockedUser.blockedUser = friend.user
                    )
                ORDER BY friend.createdAt DESC
            """
    )
    List<Friend> findAllReceivedFriendRequest(
            UUID uuid,
            @Param("status") FriendStatusCode friendStatusCode);

    Optional<Friend> findByIdAndFriendStatus(Long friendId, FriendStatusCode friendStatusCode);
}
