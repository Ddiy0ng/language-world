package com.languageworld.be.domain.post.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PostController {

    // TODO 게시글 등록: 제목, 게시글 타입, 본문(이미지 허용-최대 3장, 10MB 제한)

    // TODO 게시글 요약 검색 V1(게시글 타입에 따른 필터링-전체, 일상, 헬프 + 본문 또는 제목 검색, 최신순 정렬, 페이지네이션 + 인덱싱): 사진1, 제목, 본문20자 생략, 댓글 수, 좋아요 수

    // TODO 게시글 상세 조회: 게시글 + 댓글 본문

    // TODO 특정 사용자의 게시글 조회

    // TODO 게시글 수정

    // TODO 게시글 삭제
}
