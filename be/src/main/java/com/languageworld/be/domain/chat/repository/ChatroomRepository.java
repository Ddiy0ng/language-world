package com.languageworld.be.domain.chat.repository;

import com.languageworld.be.domain.chat.entity.Chatroom;
import com.languageworld.be.domain.chat.enumGroup.ChatroomTypeCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChatroomRepository extends JpaRepository<Chatroom, Long> {

    @Query(
            """
                SELECT chatroom
                FROM Chatroom chatroom
                WHERE chatroom.chatroomType = :type
                    AND EXISTS (
                        SELECT 1
                        FROM ChatroomUser chatroomUserAuth
                        WHERE chatroomUserAuth.chatroom = chatroom
                            AND chatroomUserAuth.user.uuid = :authUuid
                    )
                    AND EXISTS (
                        SELECT 1
                        FROM ChatroomUser chatroomUserFriend
                        WHERE chatroomUserFriend.chatroom = chatroom
                            AND chatroomUserFriend.user.uuid = :userUuid
                    )
            """
    )
    Optional<Chatroom> findByChatroomTypeCodeAndHostAndChatroomUser(@Param("type") ChatroomTypeCode chatroomType, @Param("authUuid")UUID authenticatedUserUuid, @Param("userUuid") UUID userUuid);
}
