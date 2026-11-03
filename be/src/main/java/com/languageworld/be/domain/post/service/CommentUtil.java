package com.languageworld.be.domain.post.service;

import com.languageworld.be.domain.post.entity.Comment;
import com.languageworld.be.domain.post.repository.CommentRepository;
import com.languageworld.be.global.enumGroup.CustomExceptionCode;
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
public class CommentUtil {

    private final CommentRepository commentRepository;

    public Comment getCommentByCommentId(UUID userUuid, Long commentId) {

        Optional<Comment> optionalComment = commentRepository.findById(commentId);
        if(optionalComment.isEmpty()) {

            CustomLogger.warn(LogEventCode.POST,
                    "FAIL",
                    LogEventReasonCode.COMMENT_NOT_FOUND,
                    LogEventReasonCode.COMMENT_NOT_FOUND.getMessage()
                            + " - CommentId: " + commentId,
                    userUuid.toString()
            );

            throw new CustomException(CustomExceptionCode.COMMENT_NOT_FOUND);
        }

        return optionalComment.get();
    }

    public void validateCommentAuthor(Comment comment, UUID userUuid) {

        if(!comment.getAuthor().getUuid().equals(userUuid)) {

            CustomLogger.warn(LogEventCode.POST,
                    "FAIL",
                    LogEventReasonCode.NOT_COMMENT_AUTHOR,
                    LogEventReasonCode.NOT_COMMENT_AUTHOR.getMessage()
                            + " - UserUuid: " + userUuid
                            + " - AuthorUuid: " + comment.getAuthor().getUuid(),
                    userUuid.toString()
            );

            throw new CustomException(CustomExceptionCode.NOT_COMMENT_AUTHOR);
        }
    }
}
