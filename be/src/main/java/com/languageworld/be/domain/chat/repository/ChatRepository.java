package com.languageworld.be.domain.chat.repository;

import com.languageworld.be.domain.chat.entity.Chat;
import com.languageworld.be.domain.post.entity.Post;
import com.languageworld.be.domain.post.enumGroup.PostTypeCode;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {

    @Query(
            """
                SELECT chat
                FROM Chat chat
                WHERE chat.chatroom.id = :id
                    AND EXISTS(
                        SELECT 1
                        FROM ChatroomUser chatroomUser
                        WHERE chatroomUser.chatroom = chat.chatroom
                            AND chatroomUser.user.uuid = :userUuid
                            AND chatroomUser.quitAt IS NULL
                            AND chatroomUser.enteredAt < chat.createdAt
                    )
                    AND (
                        :cursor IS NULL
                        OR chat.id < :cursor
                    )
                ORDER BY chat.id DESC
            """
    )
    Slice<Chat> findSlice(@Param("id") Long chatroomId, UUID userUuid, Pageable pageable, Long cursor);

}
