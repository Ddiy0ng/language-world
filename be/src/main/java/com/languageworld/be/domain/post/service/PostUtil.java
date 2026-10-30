package com.languageworld.be.domain.post.service;

import com.languageworld.be.domain.post.entity.Post;
import com.languageworld.be.domain.post.entity.PostImage;
import com.languageworld.be.domain.post.repository.PostImageRepository;
import com.languageworld.be.domain.post.repository.PostRepository;
import com.languageworld.be.global.enumGroup.CustomExceptionCode;
import com.languageworld.be.global.exception.CustomException;
import com.languageworld.be.global.log.CustomLogger;
import com.languageworld.be.global.log.enumGroup.LogEventCode;
import com.languageworld.be.global.log.enumGroup.LogEventReasonCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PostUtil {

    private final PostRepository postRepository;
    private final PostImageRepository postImageRepository;

    public Post getPostByPostId(UUID userUuid, Long postId) {

        Optional<Post> optionalPost = postRepository.findById(postId);
        if(optionalPost.isEmpty()) {

            CustomLogger.warn(LogEventCode.POST,
                    "FAIL",
                    LogEventReasonCode.POST_NOT_FOUND,
                    LogEventReasonCode.POST_NOT_FOUND.getMessage()
                            + " - PostId: " + postId,
                    userUuid.toString()
            );

            throw new CustomException(CustomExceptionCode.POST_NOT_FOUND);
        }

        return optionalPost.get();
    }

    public void postImageExistCheck(UUID userUuid, Long postImageId, Long postId) {

        if(postImageRepository.findByIdAndPost_Id(postImageId, postId).isEmpty()) {

            CustomLogger.warn(LogEventCode.POST,
                    "FAIL",
                    LogEventReasonCode.POST_IMAGE_NOT_FOUND,
                    LogEventReasonCode.POST_IMAGE_NOT_FOUND.getMessage() +
                            " - PostImageId: " + postImageId,
                    userUuid.toString()
            );

            throw new CustomException(CustomExceptionCode.POST_IMAGE_NOT_FOUND);
        }
    }

    public void validatePostAuthor(Post post, UUID userUuid) {

        if(!post.getAuthor().getUuid().equals(userUuid)) {

            CustomLogger.warn(LogEventCode.POST,
                    "FAIL",
                    LogEventReasonCode.NOT_POST_AUTHOR,
                    LogEventReasonCode.NOT_POST_AUTHOR.getMessage()
                            + " - UserUuid: " + userUuid
                            + " - AuthorUuid: " + post.getAuthor().getUuid(),
                    userUuid.toString()
            );

            throw new CustomException(CustomExceptionCode.NOT_POST_AUTHOR);
        }
    }

    public void  validateCount(int count, UUID userUuid) {

        // 이미지 요청 수 제한
        if (count > 3) {

            CustomLogger.warn(
                    LogEventCode.POST,
                    "FAIL",
                    LogEventReasonCode.MAXIMUM_COUNT_IS_THREE,
                    LogEventReasonCode.MAXIMUM_COUNT_IS_THREE.getMessage()
                            + " - requested count: " + count,
                    userUuid.toString()
            );

            throw new CustomException(CustomExceptionCode.MAXIMUM_COUNT_IS_THREE);
        }
    }
}
