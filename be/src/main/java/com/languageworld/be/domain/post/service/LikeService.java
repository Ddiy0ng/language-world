package com.languageworld.be.domain.post.service;

import com.languageworld.be.domain.post.dto.response.LikePostResponseDto;
import com.languageworld.be.domain.post.dto.response.IsLikedResponseDto;
import com.languageworld.be.domain.post.entity.Comment;
import com.languageworld.be.domain.post.entity.Like;
import com.languageworld.be.domain.post.entity.Post;
import com.languageworld.be.domain.post.repository.LikeRepository;
import com.languageworld.be.domain.user.entity.User;
import com.languageworld.be.domain.user.service.UserUtil;
import com.languageworld.be.global.auth.entity.CustomPrincipal;
import com.languageworld.be.global.enumGroup.CustomExceptionCode;
import com.languageworld.be.global.exception.CustomException;
import com.languageworld.be.global.log.CustomLogger;
import com.languageworld.be.global.log.enumGroup.LogEventCode;
import com.languageworld.be.global.log.enumGroup.LogEventReasonCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final UserUtil userUtil;
    private final PostUtil postUtil;
    private final LikeUtil likeUtil;

    private final int SIZE = 10;

    @Transactional
    public void createLike(CustomPrincipal customPrincipal, Long postId) {

        UUID userUuid = customPrincipal.uuid();

        if(likeRepository.existsByAuthor_UuidAndPost_Id(userUuid, postId)) {

            CustomLogger.warn(
                    LogEventCode.POST,
                    "FAIL",
                    LogEventReasonCode.ALREADY_LIKED_POST,
                    LogEventReasonCode.ALREADY_LIKED_POST.getMessage()
                        + "userUuid: " + userUuid
                        + "postId: " + postId,
                    userUuid.toString()
            );

            throw new CustomException(CustomExceptionCode.ALREADY_LIKED_POST);
        }
        User author = userUtil.getUserByUuid(userUuid);
        Post post = postUtil.getPostByPostId(userUuid, postId);

        Like like = Like.of(author, post);

        likeRepository.save(like);
    }

    @Transactional
    public List<LikePostResponseDto> findAuthenticatedUserLikePostList(CustomPrincipal customPrincipal, Long cursor) {

        UUID userUuid = customPrincipal.uuid();

        Slice<Like> likeSlice = findLike(userUuid, cursor);
        List<LikePostResponseDto> likePostResponseDtoList = likeSlice.getContent().stream()
                .map(LikePostResponseDto::from)
                .toList();

        return likePostResponseDtoList;
    }

    @Transactional
    public List<LikePostResponseDto> findSpecificUserLikePostList(UUID userUuid, Long cursor) {

        Slice<Like> likeSlice = findLike(userUuid, cursor);
        List<LikePostResponseDto> likePostResponseDtoList = likeSlice.getContent().stream()
                .map(LikePostResponseDto::from)
                .toList();

        return likePostResponseDtoList;
    }

    public IsLikedResponseDto findIsLiked(CustomPrincipal customPrincipal, Long postId) {

        UUID userUuid = customPrincipal.uuid();
        Long likeId = likeUtil.getLikeIdByPostId(userUuid, postId);
        IsLikedResponseDto isLikedResponseDto = IsLikedResponseDto.from(likeId, likeId != null);

        return isLikedResponseDto;
    }

    @Transactional
    public void removeLike(CustomPrincipal customPrincipal, Long likeId) {

        UUID userUuid = customPrincipal.uuid();
        Like like = likeUtil.getLikeByLikeId(userUuid, likeId);

        likeUtil.validateLieAuthor(userUuid, like);

        likeRepository.delete(like);
    }

    @Transactional
    private Slice<Like> findLike(UUID userUuid, Long cursor) {

        Pageable pageable = PageRequest.of(0, SIZE);
        Slice<Like> likeSlice;

        // 첫 조회: 커서x, 커서 있으면 커서 기준 조회
        if(cursor == null)
            likeSlice = likeRepository.findFirstSlice(userUuid, pageable);
        else
            likeSlice = likeRepository.findNextSlice(userUuid, pageable, cursor);

        return likeSlice;
    }
}
