package com.languageworld.be.domain.post.service;

import com.languageworld.be.domain.post.dto.request.PostCreateRequestDto;
import com.languageworld.be.domain.post.dto.request.PostImageStatusOrderDto;
import com.languageworld.be.domain.post.dto.request.PostUpdateRequestDto;
import com.languageworld.be.domain.post.dto.response.PostDetailResponseDto;
import com.languageworld.be.domain.post.dto.response.PostSummaryResponseDto;
import com.languageworld.be.domain.post.dto.response.PostSummaryCursorResponseDto;
import com.languageworld.be.domain.post.entity.Post;
import com.languageworld.be.domain.post.entity.PostImage;
import com.languageworld.be.domain.post.enumGroup.PostTypeCode;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserUtil userUtil;
    private final FileUtil fileUtil;
    private final PostUtil postUtil;


    private final int SIZE = 20;

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


    // only for test(성능 확인을 위한 것으로 실제 적용x): 페이지네이션 없이 단순 전체 조회
    // -------------------------------------------------------------------------------------------------------------
    @Transactional
    public List<PostSummaryResponseDto> findAllPostForTest(CustomPrincipal customPrincipal) {

        UUID userUuid = customPrincipal.uuid();

        List<Post> postList = postRepository.findAllByAuthor_Uuid(userUuid);
        List<PostSummaryResponseDto> postSummaryResponseDtoList = postList.stream()
                .map(post -> PostSummaryResponseDto.from(
                        post,
                        post.getCommentList().size(),
                        post.getLikeList().size()))
                .toList();

        return postSummaryResponseDtoList;
    }
    // -------------------------------------------------------------------------------------------------------------


    // 전체, 일상, 헬프 + 본문 또는 제목 검색, 최신순 정렬, 페이지네이션 + 인덱싱): 사진1, 제목, 댓글 수, 좋아요 수
    @Transactional
    public PostSummaryCursorResponseDto findAuthenticatedUserAllPost(CustomPrincipal customPrincipal, PostTypeCode postTypeCode, String keyword, Long cursor) {

        UUID userUuid = customPrincipal.uuid();

        Slice<Post> postSlice = findPost(userUuid, postTypeCode, keyword, cursor);

        List<PostSummaryResponseDto> postSummaryResponseDtoList = postSlice.getContent().stream()
                .map(post -> PostSummaryResponseDto.from(
                        post,
                        post.getCommentList().size(),
                        post.getLikeList().size()))
                .toList();

        boolean hasNext = postSlice.hasNext();

        Long nextCursor = null;
        // 다음 있을 때 커서 등록
        if(hasNext && !postSlice.isEmpty()) {

            // 마지막 요소의 인덱스를 다음 커서로
            nextCursor = postSlice.getContent().get(postSlice.getNumberOfElements() - 1).getId();
        }

        return PostSummaryCursorResponseDto.from(nextCursor, hasNext, postSummaryResponseDtoList);
    }

    @Transactional
    public PostSummaryCursorResponseDto findSpecificUserAllPost(CustomPrincipal customPrincipal, UUID authorUuid, PostTypeCode postTypeCode, String keyword, Long cursor) {

        Slice<Post> postSlice = findPost(authorUuid, postTypeCode, keyword, cursor);

        List<PostSummaryResponseDto> postSummaryResponseDtoList = postSlice.getContent().stream()
                .map(post -> PostSummaryResponseDto.from(
                        post,
                        post.getCommentList().size(),
                        post.getLikeList().size()))
                .toList();

        boolean hasNext = postSlice.hasNext();

        Long nextCursor = null;
        if(hasNext && !postSlice.isEmpty())
            nextCursor = postSlice.getContent().get(postSlice.getNumberOfElements() - 1).getId();

        return PostSummaryCursorResponseDto.from(nextCursor, hasNext, postSummaryResponseDtoList);
    }

    @Transactional
    public PostSummaryCursorResponseDto searchPost(CustomPrincipal customPrincipal, PostTypeCode postTypeCode, String keyword, Long cursor) {

        Slice<Post> postSlice = findPost(null, postTypeCode, keyword, cursor);

        List<PostSummaryResponseDto> postSummaryResponseDtoList = postSlice.getContent().stream()
                .map(post -> PostSummaryResponseDto.from(
                        post,
                        post.getCommentList().size(),
                        post.getLikeList().size()))
                .toList();

        boolean hasNext = postSlice.hasNext();

        Long nextCursor = null;
        if(hasNext && !postSlice.isEmpty())
            nextCursor = postSlice.getContent().get(postSlice.getNumberOfElements() - 1).getId();


        return PostSummaryCursorResponseDto.from(nextCursor, hasNext, postSummaryResponseDtoList);
    }

    @Transactional
    public PostDetailResponseDto findPostDetail(CustomPrincipal customPrincipal, Long postId) {

        UUID userUuid = customPrincipal.uuid();

        Post post = postUtil.getPostByPostId(userUuid, postId);

        return PostDetailResponseDto.from(post, post.getCommentList().size(), post.getLikeList().size());
    }

    @Transactional
    public void changePost(CustomPrincipal customPrincipal, Long postId, PostUpdateRequestDto postUpdateRequestDto, List<MultipartFile> multipartFileList) {

        UUID userUuid = customPrincipal.uuid();

        /*
             1. 게시글 조회 및 작성자 검증
             2. 이미지 요청 정보 조회 및 null에 대한 처리: 이미지 요청 정보 null이지만 요청 이미지 있으면 예외
             3. 이미지 DTO, 파일 유효성 및 개수 검증: 3개 이하, not null, 빈 슬롯 < 요청 이미지 불가
             4. 기존 이미지 ID 검증 및 중복 확인: postImageId의 해당 포스트 존재 여부 확인, 중복 요청 확인(중복 불가)
             5. 트랜잭션 커밋, 롤백 시 파일 삭제 리스트: 커밋 시 삭제 파일 경로 삭제, 롤백 시 업로드 파일 경로 삭제
             6. 삭제 대상 이미지 제거 및 파일 경로 저장: db에서 경로 삭제, 커밋 리스트에 추가
             7. 기존 이미지 순서 변경 및 신규 이미지 업로드: 빈 슬롯 다 채울 필요x - 건너뜀, 연속 저장, 롤백 리스트에 추가
             8. 게시글 제목 및 내용 수정
         */

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
            for (MultipartFile multipartFile : nullSafeMultipartFileList) {

                if (multipartFile == null || multipartFile.isEmpty()) {

                    CustomLogger.warn(
                            LogEventCode.POST,
                            "FAIL",
                            LogEventReasonCode.CANT_UPLOAD_EMPTY_MULTIPART_FILE,
                            LogEventReasonCode.CANT_UPLOAD_EMPTY_MULTIPART_FILE.getMessage(),
                            userUuid.toString()
                    );

                    throw new CustomException(CustomExceptionCode.CANT_UPLOAD_EMPTY_MULTIPART_FILE);
                }
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

            // 빈 슬롯 보다 요청 이미지가 많으면 예외
            if (emptySlotCount < requestImageCount) {

                CustomLogger.warn(
                        LogEventCode.POST,
                        "FAIL",
                        LogEventReasonCode.REQUESTED_IMG_LARGER_THAN_EMPTY_SLOT,
                        LogEventReasonCode.REQUESTED_IMG_LARGER_THAN_EMPTY_SLOT.getMessage()
                                + " - Empty slot count: " + emptySlotCount
                                + " - requested img count: " + requestImageCount,
                        userUuid.toString()
                );

                throw new CustomException(CustomExceptionCode.REQUESTED_IMG_LARGER_THAN_EMPTY_SLOT);
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

                    // 중복된 이미지 id 지정 불가
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
                    }
                }
            }

            // 삭제할 이미지 리스트 - 커밋, 롤백 (db에서 삭제 / 실제 삭제와 분리하여 이는 나중에 진행)
            List<String> commitDeleteFilePathList = new ArrayList<>();
            List<String> rollbackDeleteFilePathList = new ArrayList<>();

            fileUtil.registerCleanUp(userUuid, FilePurposeCode.POST, commitDeleteFilePathList, rollbackDeleteFilePathList);

            for (PostImage postImage : new ArrayList<>(post.getPostImageList())) {

                //postImageMap
                if (!notDeleteImageIdSet.contains(postImage.getId())) {

                    // 나중에 실제 삭제 위해 경로 보관(커밋 시)
                    commitDeleteFilePathList.add(postImage.getPostImageUrl());

                    // db에서 삭제
                    post.removePostImage(postImage);
                }
            }

            // 이미지 업로드
            int fileIndex = 0;
            int order = 0;
            for (PostImageStatusOrderDto postImageStatusOrderDto : postImageStatusOrderDtoList) {

                // 유지할 이미지 아이디
                Long postImageId = postImageStatusOrderDto.imageId();

                // null 아님: 순서 변경
                if (postImageId != null) {
                    oldPostImageMap.get(postImageId).changeOrder(order++);
                }
                // null: 비우거나 새 이미지 삽입
                else {

                    // 다음 파일 인덱스(+1은 수) 요청받은 이미지 수와 돋일하거나 크면 더 이상 업로드x(넘김)
                    if(fileIndex >= requestImageCount)
                        continue;

                    // 빈 슬롯만큼 요청 사진이 있을 때 업로드
                    String filePath = fileUtil.uploadFile(nullSafeMultipartFileList.get(fileIndex), FilePurposeCode.POST);

                    // 롤백 시 삭제할 리스트 추가(새로 업로드한 파일 경로 삭제)
                    rollbackDeleteFilePathList.add(filePath);

                    PostImage postImage = PostImage.of(filePath, order++, post);
                    post.addPostImage(postImage);

                    fileIndex++;
                }
            }
        }
        // 게시글 텍스트 수정
        post.changePostTitle(postUpdateRequestDto.title());
        post.changePostContentText(postUpdateRequestDto.contentText());
    }

    @Transactional
    public void removePost(CustomPrincipal customPrincipal, Long postId) {

        UUID userUuid = customPrincipal.uuid();

        // 게시글 조회
        Post post = postUtil.getPostByPostId(userUuid, postId);

        // 작성자 검증
        postUtil.validatePostAuthor(post, userUuid);

        // 삭제 파일 경로
        List<String> commitDeleteFilePathList = post.getPostImageList().stream()
                        .map(PostImage::getPostImageUrl)
                        .toList();

        // 커밋 시 실제 삭제
        fileUtil.registerCleanUp(userUuid, FilePurposeCode.POST, commitDeleteFilePathList, List.of());

        postRepository.delete(post);
    }

    @Transactional
    private Slice<Post> findPost(UUID userUuid, PostTypeCode postTypeCode, String keyword, Long cursor) {

        Pageable pageable = PageRequest.of(0, SIZE);
        Slice<Post> postSlice;

        // 특정 사용자x
        if(userUuid == null) {
            if(cursor == null)
                postSlice = postRepository.findFirstSlice(postTypeCode, keyword, pageable);
            else
                postSlice = postRepository.findNextSlice(postTypeCode, keyword, pageable, cursor);

            return postSlice;
        }

        // 첫 조회: 커서x, 커서 있으면 커서 기준 조회
        if(cursor == null)
            postSlice = postRepository.findFirstSlice(userUuid, postTypeCode, keyword, pageable);
        else
            postSlice = postRepository.findNextSlice(userUuid, postTypeCode, keyword, pageable, cursor);

        return postSlice;
    }
}