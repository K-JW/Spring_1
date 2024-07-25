package org.portfolio.spring_1.controller.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.portfolio.spring_1.dto.ArticleRequestDTO;
import org.portfolio.spring_1.dto.ArticleResponseDTO;
import org.portfolio.spring_1.dto.CustomOAuth2Member;
import org.portfolio.spring_1.service.ArticleService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api")
public class ArticleApiController {

    private final ArticleService articleService;

    // 게시글 작성
    @PostMapping("/articles/new")
    public ResponseEntity<ArticleResponseDTO> createArticle(@RequestBody ArticleRequestDTO articleRequestDTO, @AuthenticationPrincipal CustomOAuth2Member customOAuth2Member) {

        log.info("requestDTO = {}", articleRequestDTO.toString());

        String authorSerial = customOAuth2Member.getSerial();

        log.info("getAuthorSerial = {}", authorSerial);

        ArticleRequestDTO authenticatedArticleRequestDTO = ArticleRequestDTO.builder()
                .title(articleRequestDTO.getTitle())
                .content(articleRequestDTO.getContent())
                .authorSerial(authorSerial)
                .build();

        ArticleResponseDTO createdArticle = articleService.create(authenticatedArticleRequestDTO);

        return ResponseEntity.ok().body(createdArticle);
    }

    // 게시글 수정
//    @PatchMapping("/articles/update")
//    public ResponseEntity<Article>

    // 게시글 삭제
}
