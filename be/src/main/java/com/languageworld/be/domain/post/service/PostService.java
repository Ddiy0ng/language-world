package com.languageworld.be.domain.post.service;

import com.languageworld.be.domain.post.dto.request.PostCreateRequestDto;
import com.languageworld.be.domain.post.dto.request.PostImageStatusOrderDto;
import com.languageworld.be.domain.post.dto.request.PostUpdateRequestDto;
import com.languageworld.be.domain.post.dto.response.PostResponseDto;
import com.languageworld.be.domain.post.dto.response.PostSummaryResponseDto;
import com.languageworld.be.domain.post.entity.Post;
import com.languageworld.be.domain.post.entity.PostImage;
import com.languageworld.be.domain.post.repository.PostImageRepository;
import com.languageworld.be.domain.post.repository.PostRepository;
import com.languageworld.be.domain.user.entity.User;
import com.languageworld.be.domain.user.service.UserUtil;
import com.languageworld.be.global.auth.entity.CustomPrincipal;
import com.languageworld.be.global.enumGroup.CustomExceptionCode;
import com.languageworld.be.global.exception.CustomException;
import com.languageworld.be.global.log.CustomLogger;
import com.languageworld.be.global.log.enumGroup.LogEventCode;
import com.languageworld.be.global.log.enumGroup.LogEventReasonCode;
import com.languageworld.be.global.multiPartFileUtil.FileUtil;
import com.languageworld.be.global.multiPartFileUtil.enumGroup.FilePurposeCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostImageRepository postImageRepository;
    private final UserUtil userUtil;
    private final FileUtil fileUtil;
    private final PostUtil postUtil;

    @Transactional
    public void createPost(CustomPrincipal customPrincipal, PostCreateRequestDto postCreateRequestDto, List<MultipartFile> multipartFileList) {

        UUID userUuid = customPrincipal.uuid();
        User author = userUtil.getUserByUuid(userUuid);
        Post post = Post.of(postCreateRequestDto, author);

        // 요청 받은 이미지가 있을 경우에만 진행
        if(multipartFileList != null && !multipartFileList.isEmpty()) {

            // 이미지 요청 수 제한
            postUtil.validateCount(multipartFileList.size(), userUuid);

            // 이미지 업로드
            List<String> rollbackDeleteFilePathList = new ArrayList<>();
            fileUtil.registerCleanUp(userUuid, FilePurposeCode.POST, List.of(), rollbackDeleteFilePathList);
            for (int order = 0; order < multipartFileList.size(); order++) {

                String filePath = fileUtil.uploadFile(multipartFileList.get(order), FilePurposeCode.POST);

                rollbackDeleteFilePathList.add(filePath);

                PostImage postImage = PostImage.of(filePath, order, post);
                post.addPostImage(postImage);
            }
        }

        postRepository.save(post);
    }

    @Transactional
    public List<PostSummaryResponseDto> findAuthenticatedUserAllPost(CustomPrincipal customPrincipal) {

        UUID userUuid = customPrincipal.uuid();

    }

    @Transactional
    public List<PostSummaryResponseDto> findSpecificUserAllPost(CustomPrincipal customPrincipal, UUID userUuid) {


    }

    @Transactional
    public List<PostSummaryResponseDto> searchPost(CustomPrincipal customPrincipal, String keyword) {


    }

    @Transactional
    public PostResponseDto findPostDetail(CustomPrincipal customPrincipal, Long postId) {


    }

    @Transactional
    public void changePost(CustomPrincipal customPrincipal, Long postId, PostUpdateRequestDto postUpdateRequestDto, List<MultipartFile> multipartFileList) {

        UUID userUuid = customPrincipal.uuid();

        // 게시글 조회
        Post post = postUtil.getPostByPostId(userUuid, postId);

        // 작성자 검증
        postUtil.validatePostAuthor(post, userUuid);

        //이미지 요청 정보
        List<PostImageStatusOrderDto> postImageStatusOrderDtoList = postUpdateRequestDto.postImageStatusOrderDtoList();
        List<MultipartFile> nullSafeMultipartFileList = multipartFileList == null ? List.of() : multipartFileList;

        // 요청 이미지 있으나 이미지 관련 정보 없으면 예외
        if(postImageStatusOrderDtoList == null) {

            if(!nullSafeMultipartFileList.isEmpty()) {

                CustomLogger.warn(
                        LogEventCode.POST,
                        "FAIL",
                        LogEventReasonCode.IMAGE_STATUS_ORDER_LIST_REQUIRED,
                        LogEventReasonCode.IMAGE_STATUS_ORDER_LIST_REQUIRED.getMessage(),
                        userUuid.toString()
                );

                throw new CustomException(CustomExceptionCode.IMAGE_STATUS_ORDER_LIST_REQUIRED);
            }
        } else {

            // 이미지 관련 정보 null 불가
            for (PostImageStatusOrderDto postImageStatusOrderDto : postImageStatusOrderDtoList) {

                if (postImageStatusOrderDto == null) {

                    CustomLogger.warn(
                            LogEventCode.POST,
                            "FAIL",
                            LogEventReasonCode.IMAGE_STATUS_ORDER_REQUIRED,
                            LogEventReasonCode.IMAGE_STATUS_ORDER_REQUIRED.getMessage(),
                            userUuid.toString()
                    );

                    throw new CustomException(CustomExceptionCode.IMAGE_STATUS_ORDER_REQUIRED);
                }
            }

            // 빈 multipartFile 불가
            if(nullSafeMultipartFileList.stream().anyMatch(multipartFile -> multipartFile == null || multipartFile.isEmpty())) {

                CustomLogger.warn(
                        LogEventCode.POST,
                        "FAIL",
                        LogEventReasonCode.CANT_UPLOAD_EMPTY_MULTIPART_FILE,
                        LogEventReasonCode.CANT_UPLOAD_EMPTY_MULTIPART_FILE.getMessage(),
                        userUuid.toString()
                );

                throw new CustomException(CustomExceptionCode.CANT_UPLOAD_EMPTY_MULTIPART_FILE);
            }

            // 정보, 이미지 개수 3이하
            postUtil.validateCount(postImageStatusOrderDtoList.size(), userUuid);
            postUtil.validateCount(nullSafeMultipartFileList.size(), userUuid);

            //empty file slot 개수 확인
            long emptySlotCount = postImageStatusOrderDtoList.stream()
                    .filter(postImageStatusOrderDto -> postImageStatusOrderDto.imageId() == null)
                    .count();

            //request image 개수 확인
            long requestImageCount = nullSafeMultipartFileList.size();

            // 빈 슬롯, 요청 이미지 개수 일치 확인
            if (emptySlotCount != requestImageCount) {

                CustomLogger.warn(
                        LogEventCode.POST,
                        "FAIL",
                        LogEventReasonCode.EMPTY_SLOT_AND_NEW_IMG_SIZE_NOT_MATCH,
                        LogEventReasonCode.EMPTY_SLOT_AND_NEW_IMG_SIZE_NOT_MATCH.getMessage()
                                + " - Empty slot count: " + emptySlotCount
                                + " - requested img count: " + requestImageCount,
                        userUuid.toString()
                );

                throw new CustomException(CustomExceptionCode.EMPTY_SLOT_AND_NEW_IMG_SIZE_NOT_MATCH);
            }

            // 기존 이미지 리스트
            Map<Long, PostImage> oldPostImageMap = post.getPostImageList().stream()
                    .collect(Collectors.toMap(PostImage::getId, postImage -> postImage));

            // 기존 이미지 id 검증(요청받은 ID가 존재하는 PostImage의 것인가), 삭제 안 할 사진 id 저장
            Set<Long> notDeleteImageIdSet = new HashSet<>();
            for (PostImageStatusOrderDto postImageStatusOrderDto : postImageStatusOrderDtoList) {

                Long postImageId = postImageStatusOrderDto.imageId();
                if (postImageId != null) {

                    // 해당 게시글의 이미지인지 확인
                    postUtil.postImageExistCheck(userUuid, postImageId, postId);

                    // 중복된 이미지 id 저장 불가
                    if (!notDeleteImageIdSet.add(postImageId)) {

                        CustomLogger.warn(
                                LogEventCode.POST,
                                "FAIL",
                                LogEventReasonCode.DUPLICATE_IMAGE_ID,
                                LogEventReasonCode.DUPLICATE_IMAGE_ID.getMessage()
                                        + " - Duplicate imageId: " + postImageId,
                                userUuid.toString()
                        );

                        throw new CustomException(CustomExceptionCode.DUPLICATE_IMAGE_ID);
                    };
                }
            }

            // 삭제할 이미지 삭제(db에서 삭제 / 실제 삭제와 분리하여 이는 나중에 진행)
            List<String> commitDeleteFilePathList = new ArrayList<>();
            List<String> rollbackDeleteFilePath = new ArrayList<>();

            fileUtil.registerCleanUp(userUuid, FilePurposeCode.POST, commitDeleteFilePathList, rollbackDeleteFilePath);

            for (PostImage postImage : new ArrayList<>(post.getPostImageList())) {

                //postImageMap
                if (!notDeleteImageIdSet.contains(postImage.getId())) {

                    // 나중에 삭제 위해 경로 보관
                    commitDeleteFilePathList.add(postImage.getPostImageUrl());

                    // db에서 삭제
                    post.removePostImage(postImage);
                }
            }

            // 이미지 업로드
            int count = 0;
            for (int i = 0; i < postImageStatusOrderDtoList.size(); i++) {
                Long postImageId = postImageStatusOrderDtoList.get(i).imageId();
                if (postImageId != null)
                    oldPostImageMap.get(postImageId).changeOrder(i);
                if (postImageId == null) {
                    String filePath = fileUtil.uploadFile(nullSafeMultipartFileList.get(count), FilePurposeCode.POST);

                    rollbackDeleteFilePath.add(filePath);

                    PostImage postImage = PostImage.of(filePath, i, post);
                    post.addPostImage(postImage);

                    count++;
                }
            }
        }
        // 게시글 텍스트 수정
        post.changePostTitle(postUpdateRequestDto.title());
        post.changePostContentText(postUpdateRequestDto.contentText());
    }

    @Transactional
    public void removePost(CustomPrincipal customPrincipal, Long postId) {


    }
}