package com.languageworld.be.domain.chat.repository;

import com.languageworld.be.domain.chat.entity.Chatroom;
import com.languageworld.be.domain.chat.enumGroup.ChatroomTypeCode;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
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

    @Query(
            """
                SELECT chatroom
                FROM Chatroom chatroom
                WHERE
                    EXISTS (
                        SELECT 1
                        FROM ChatroomUser chatroomUser
                        WHERE chatroomUser.chatroom = chatroom
                            AND chatroomUser.user.uuid = :userUuid
                            AND chatroomUser.quitAt IS NULL
                            AND (
                                :keyword IS NULL
                                OR LOWER(chatroom.chatroomTitle) LIKE LOWER(CONCAT('%', :keyword, '%'))
                                OR EXISTS(
                                    SELECT member
                                    FROM ChatroomUser member
                                    WHERE member.chatroom = chatroom
                                        AND member.user.uuid <> :userUuid
                                        AND member.quitAt IS NULL
                                        AND LOWER(member.user.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
                                )
                                OR EXISTS (
                                    SELECT 1
                                    FROM Chat chat
                                    WHERE chat.chatroom = chatroom
                                        AND chat.createdAt > chatroomUser.enteredAt
                                        AND LOWER(chat.chatMessage) LIKE LOWER(CONCAT('%', :keyword, '%'))
                                )
                            )
                    )
                    AND (
                        :cursor IS NULL
                        OR chatroom.id < :cursor
                    )
                ORDER BY chatroom.id DESC
            """
    )
    Slice<Chatroom> findSlice(UUID userUuid, String keyword, Pageable pageable, Long cursor);
}
