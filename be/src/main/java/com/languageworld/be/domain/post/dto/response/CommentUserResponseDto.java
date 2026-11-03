package com.languageworld.be.domain.post.dto.response;

import com.languageworld.be.domain.post.entity.Comment;
import com.languageworld.be.domain.user.dto.response.UserSummaryResponseDto;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CommentUserResponseDto(

        // TODO 인증된 사용자가 작성한 댓글 전체 조회(postId + 게시글 제목 + 댓글 + 댓글 id)
        UserSummaryResponseDto userSummaryResponseDto,
        Long postId,
        String title,
        Long commentId,
        String content,
        LocalDateTime modifiedAt
) {
    public static CommentUserResponseDto from(Comment comment) {

        CommentUserResponseDto commentUserResponseDto = CommentUserResponseDto.builder()
                .userSummaryResponseDto(UserSummaryResponseDto.from(comment.getAuthor()))
                .postId(comment.getPost().getId())
                .title(comment.getPost().getTitle())
                .commentId(comment.getId())
                .content(comment.getContentText())
                .modifiedAt(comment.getModifiedAt())
                .build();

        return commentUserResponseDto;
    }
}
