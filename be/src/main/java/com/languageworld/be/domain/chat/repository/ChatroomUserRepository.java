package com.languageworld.be.domain.chat.repository;

import com.languageworld.be.domain.chat.entity.ChatroomUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChatroomUserRepository extends JpaRepository<ChatroomUser, Long> {

    Optional<ChatroomUser> findByChatroom_IdAndUser_Uuid(Long chatroomId, UUID userUuid);

    boolean existsByChatroom_IdAndUser_Uuid(Long chatroomId, UUID userUuid);

    Optional<ChatroomUser> findFirstByChatroom_IdAndUser_UuidNotAndQuitAtIsNullOrderByEnteredAtAsc(Long chatroomId, UUID userUuid);
}
