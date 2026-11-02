package com.languageworld.be.domain.post.dto.response;

import com.languageworld.be.domain.post.entity.Comment;
import com.languageworld.be.domain.user.dto.response.UserSummaryResponseDto;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PostCommentResponseDto(

        UserSummaryResponseDto userSummaryResponseDto,
        String title,
        Long commentId,
        String content,
        LocalDateTime modifiedAt
) {

    public static PostCommentResponseDto from(Comment comment) {

        PostCommentResponseDto postCommentResponseDto = PostCommentResponseDto.builder()
                .userSummaryResponseDto(UserSummaryResponseDto.from(comment.getAuthor()))
                .title(comment.getPost().getTitle())
                .commentId(comment.getId())
                .content(comment.getContentText())
                .modifiedAt(comment.getModifiedAt())
                .build();

        return postCommentResponseDto;
    }
}
