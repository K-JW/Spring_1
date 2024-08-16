package org.portfolio.spring_1.controller.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.portfolio.spring_1.dto.CommentRequestDTO;
import org.portfolio.spring_1.dto.CommentResponseDTO;
import org.portfolio.spring_1.dto.CustomOAuth2Member;
import org.portfolio.spring_1.entity.Article;
import org.portfolio.spring_1.entity.Member;
import org.portfolio.spring_1.service.ArticleService;
import org.portfolio.spring_1.service.CommentService;
import org.portfolio.spring_1.service.CustomOAuth2MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/comments")
public class CommentApiController {

    private final CommentService commentService;
    private final CustomOAuth2MemberService memberService;
    private final ArticleService articleService;

    @PostMapping("/new")
    public ResponseEntity<String> createComment(@RequestBody CommentRequestDTO commentRequestDTO, @AuthenticationPrincipal CustomOAuth2Member customOAuth2Member) {

        String getLoggedInMemberSerial = customOAuth2Member.getSerial();

        Member getLoggedInMember = memberService.getMemberBySerial(getLoggedInMemberSerial);

        Article getArticle = articleService.getArticleById(commentRequestDTO.getArticleId());

        CommentResponseDTO createdComment = commentService.create(commentRequestDTO, getArticle, getLoggedInMember);

        String redirectUrl = "/articles/" + createdComment.getArticleId();

        return ResponseEntity.ok(redirectUrl);
    }

    @PatchMapping("/edit/{commentId}")
    public ResponseEntity<String> updateComment(@PathVariable Long commentId, @RequestBody CommentRequestDTO commentRequestDTO, @AuthenticationPrincipal CustomOAuth2Member customOAuth2Member) {

        String getLoggedInMemberSerial = customOAuth2Member.getSerial();
        Member getLoggedInMember = memberService.getMemberBySerial(getLoggedInMemberSerial);

        Article getArticle = articleService.getArticleById(commentRequestDTO.getArticleId());

        CommentResponseDTO updatedComment = commentService.update(commentId, commentRequestDTO, getLoggedInMember, getArticle);

        String redirectUrl = "/articles/" + updatedComment.getArticleId();

        return ResponseEntity.ok(redirectUrl);
    }

    @DeleteMapping("/delete/{commentId}")
    public ResponseEntity<CommentResponseDTO> deleteComment(@PathVariable Long commentId, @AuthenticationPrincipal CustomOAuth2Member customOAuth2Member) {

        String getLoggedInMemberSerial = customOAuth2Member.getSerial();
        memberService.getMemberBySerial(getLoggedInMemberSerial);

        commentService.deleteByCommentId(commentId);

        return ResponseEntity.ok().build();
    }
}