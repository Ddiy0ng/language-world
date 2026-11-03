package com.languageworld.be.domain.post.service;

import com.languageworld.be.domain.post.entity.Like;
import com.languageworld.be.domain.post.repository.LikeRepository;
import com.languageworld.be.global.exception.enumGroup.CustomExceptionCode;
import com.languageworld.be.global.exception.CustomException;
import com.languageworld.be.global.log.CustomLogger;
import com.languageworld.be.global.log.enumGroup.LogEventCode;
import com.languageworld.be.global.log.enumGroup.LogEventReasonCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class LikeUtil {

    private final LikeRepository likeRepository;

    public Long getLikeIdByPostId(UUID userUuid, Long postId) {

        Optional<Long> optionalLikeId = likeRepository.findIdByPost_IdAndAuthor_Uuid(postId, userUuid);

        if(optionalLikeId.isEmpty())
            return null;

        return optionalLikeId.get();
    }

    public Like getLikeByLikeId(UUID userUuid, Long likeId) {

        Optional<Like> optionalLike = likeRepository.findById(likeId);
        if(optionalLike.isEmpty()) {

            CustomLogger.warn(LogEventCode.POST,
                    "FAIL",
                    LogEventReasonCode.LIKE_NOT_FOUND,
                    LogEventReasonCode.LIKE_NOT_FOUND.getMessage()
                            + " - likeId: " + likeId,
                    userUuid.toString()
            );

            throw new CustomException(CustomExceptionCode.LIKE_NOT_FOUND);
        }

        return optionalLike.get();
    }

    public void validateLieAuthor(UUID userUuid, Like like) {

        if(!like.getAuthor().getUuid().equals(userUuid)) {

            CustomLogger.warn(LogEventCode.POST,
                    "FAIL",
                    LogEventReasonCode.NOT_LIKE_AUTHOR,
                    LogEventReasonCode.NOT_LIKE_AUTHOR.getMessage()
                            + " - UserUuid: " + userUuid
                            + " - AuthorUuid: " + like.getAuthor().getUuid(),
                    userUuid.toString()
            );

            throw new CustomException(CustomExceptionCode.NOT_LIKE_AUTHOR);
        }
    }
}
