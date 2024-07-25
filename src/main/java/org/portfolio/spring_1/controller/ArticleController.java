package org.portfolio.spring_1.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.portfolio.spring_1.dto.ArticleRequestDTO;
import org.portfolio.spring_1.dto.ArticleResponseDTO;
import org.portfolio.spring_1.dto.CommentResponseDTO;
import org.portfolio.spring_1.dto.CustomOAuth2Member;
import org.portfolio.spring_1.entity.Member;
import org.portfolio.spring_1.service.ArticleService;
import org.portfolio.spring_1.service.CommentService;
import org.portfolio.spring_1.service.CustomOAuth2MemberService;
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
    public String getArticleList(Model model) {
        List<ArticleResponseDTO> articleList = articleService.getAll();

        model.addAttribute("articleList", articleList);

        return "articles/articleList";
    }

    // 특정 게시글 조회 페이지 반환
    @GetMapping("/{articleId}")
    public String getArticle(@PathVariable Long articleId, @AuthenticationPrincipal CustomOAuth2Member customOAuth2Member, Model model) {

        String memberSerial = customOAuth2Member.getSerial();
        Member member = memberService.getMemberBySerial(memberSerial);

        ArticleResponseDTO article = articleService.getArticleByIdAndSerial(articleId, memberSerial, false);

        List<CommentResponseDTO> commentList = commentService.getCommentListByArticleId(articleId);

        model.addAttribute("loggedInMember", member.getSerial());
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

        String memberSerial = customOAuth2Member.getSerial();

        ArticleResponseDTO article = articleService.getArticleByIdAndSerial(id, memberSerial, true);

        model.addAttribute("article", article);
        model.addAttribute("authorSerial", memberSerial);

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
