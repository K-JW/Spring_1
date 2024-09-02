package org.portfolio.spring_1.controller;

import lombok.RequiredArgsConstructor;
import org.portfolio.spring_1.dto.ArticleRequestDTO;
import org.portfolio.spring_1.dto.ArticleResponseDTO;
import org.portfolio.spring_1.dto.CommentResponseDTO;
import org.portfolio.spring_1.dto.CustomOAuth2Member;
import org.portfolio.spring_1.entity.Member;
import org.portfolio.spring_1.service.ArticleService;
import org.portfolio.spring_1.service.CommentService;
import org.portfolio.spring_1.service.CustomOAuth2MemberService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/articles")
public class ArticleController {

    private final ArticleService articleService;
    private final CommentService commentService;
    private final CustomOAuth2MemberService memberService;

    // 전체 게시글 조회 페이지 반환
    @GetMapping
    public String getArticleList(@PageableDefault(page = 1) Pageable pageable, @AuthenticationPrincipal CustomOAuth2Member oAuth2Member, Model model) {
        String getLoggedInMemberSerial = oAuth2Member.getSerial();
        Page<ArticleResponseDTO> articleList = articleService.getAll(pageable);

        int blockLimit = 10;
        int startPage = (((int) Math.ceil(((double) pageable.getPageNumber() / blockLimit))) -1) * blockLimit + 1;
        int endPage = Math.min((startPage + blockLimit-1), articleList.getTotalPages());

        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("loggedInMemberSerial", getLoggedInMemberSerial);
        model.addAttribute("articleList", articleList);

        return "articles/articleList";
    }

    // 특정 게시글 조회 페이지 반환
    @GetMapping("/{articleId}")
    public String getArticle(@PathVariable Long articleId, @PageableDefault(page=1) Pageable pageable, @AuthenticationPrincipal CustomOAuth2Member customOAuth2Member, Model model) {

        String getLoggedInMemberSerial = customOAuth2Member.getSerial();
        Member member = memberService.getMemberBySerial(getLoggedInMemberSerial);

        ArticleResponseDTO article = articleService.getAndReturnArticleByIdAndSerial(articleId,false);

        Page<CommentResponseDTO> commentList = commentService.getCommentListByArticleId(articleId, pageable);
        List<CommentResponseDTO> replyList = commentService.getReplyListByArticleId(articleId);

        int blockLimit = 10;
        int startPage = (((int) Math.ceil(((double) pageable.getPageNumber() / blockLimit))) -1) * blockLimit + 1;
        int endPage = Math.min((startPage + blockLimit-1), commentList.getTotalPages());

        model.addAttribute("commentList", commentList);
        model.addAttribute("replyList", replyList);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("loggedInMemberSerial", getLoggedInMemberSerial);
        model.addAttribute("article", article);
        model.addAttribute("commentList", commentList);

        return "articles/article";
    }

    // 게시글 작성 페이지 반환
    @GetMapping("/new")
    public String getArticleForm(@AuthenticationPrincipal CustomOAuth2Member customOAuth2Member, Model model) {
        String authorSerial = customOAuth2Member.getSerial();
        model.addAttribute("authorSerial", authorSerial);
        return "articles/new";
    }

    // 게시글 작성 후 조회 페이지 반환
    @PostMapping("/create")
    public String createArticle(ArticleRequestDTO articleRequestDTO) {
        ArticleResponseDTO articleResponseDTO = articleService.create(articleRequestDTO);
        Long articleId = articleResponseDTO.getId();
        return "redirect:/articles/" + articleId;
    }

    // 게시글 수정 페이지 반환
    @GetMapping("/edit/{id}")
    public String getArticleEditForm(@PathVariable Long id, @AuthenticationPrincipal CustomOAuth2Member customOAuth2Member, Model model) {

        String getLoggedInMemberSerial = customOAuth2Member.getSerial();

        ArticleResponseDTO article = articleService.getAndReturnArticleByIdAndSerial(id, true);

        model.addAttribute("loggedInMemberSerial", getLoggedInMemberSerial);
        model.addAttribute("article", article);

        return "articles/edit";
    }

    // 게시글 수정 후 조회 페이지 반환
    @PostMapping("/update/{id}")
    public String updateArticle(@PathVariable Long id, ArticleRequestDTO articleRequestDTO) {

        ArticleResponseDTO updatedArticle = articleService.update(id, articleRequestDTO);

        Long articleId = updatedArticle.getId();

        return "redirect:/articles/" + articleId;
    }

    // 게시글 삭제
    @GetMapping("/delete/{id}")
    public String deleteArticle(@PathVariable Long id) {

        commentService.deleteByArticleId(id);
        articleService.delete(id);

        return "redirect:/articles";
    }

}
