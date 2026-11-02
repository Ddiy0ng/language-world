package com.languageworld.be.domain.post.dto.response;

import com.languageworld.be.domain.post.entity.Post;
import com.languageworld.be.domain.post.enumGroup.PostTypeCode;
import com.languageworld.be.domain.user.enumGroup.NationCode;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record PostResponseDto (

        UUID authorUuid,
        String authorProfileImageUrl,
        String authorName,
        NationCode nationCode,
        String learningLanguage,
        String level,
        PostTypeCode postTypeCode,
        Long postId,
        String title,
        List<PostImageResponseDto> postImageUrlList,
        LocalDateTime modifiedAt,
        long commentSize,
        long likeSize
){

    public static PostResponseDto from(Post post, long commentSize, long likeSize) {

        PostResponseDto postResponseDto = PostResponseDto.builder()
                .authorUuid(post.getAuthor().getUuid())
                .authorProfileImageUrl(post.getAuthor().getProfileImageUrl())
                .authorName(post.getAuthor().getName())
                .nationCode(post.getAuthor().getNation())
                .learningLanguage(post.getAuthor().getLearningLanguage().getType())
                .level(post.getAuthor().getLearningLanguage().getType())
                .postTypeCode(post.getPostType())
                .postId(post.getId())
                .title(post.getTitle())
                .postImageUrlList(post.getPostImageList().isEmpty()?List.of():post.getPostImageList().stream().map(PostImageResponseDto::from).toList())
                .modifiedAt(post.getModifiedAt())
                .commentSize(commentSize)
                .likeSize(likeSize)
                .build();

        return postResponseDto;
    }
}
