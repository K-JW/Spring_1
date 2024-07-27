package org.portfolio.spring_1.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.portfolio.spring_1.dto.ArticleRequestDTO;
import org.portfolio.spring_1.dto.ArticleResponseDTO;
import org.portfolio.spring_1.entity.Article;
import org.portfolio.spring_1.entity.Member;
import org.portfolio.spring_1.mapper.ArticleMapper;
import org.portfolio.spring_1.repository.ArticleRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final CommentService commentService;
    private final CustomOAuth2MemberService memberService;
    private final ArticleMapper articleMapper;

    public List<ArticleResponseDTO> getAll() {

        List<ArticleResponseDTO> articlesWithCommentCount = new ArrayList<>();

        List<Article> getArticleList = articleRepository.findAll();

        if (getArticleList.isEmpty()) {
            return articlesWithCommentCount;
        }

        for (Article article : getArticleList) {
            Long articleId = article.getId();

            int commentCount = commentService.getCommentCountByArticleId(articleId);
            ArticleResponseDTO responseDTO = articleMapper.toDTO(article);
            responseDTO.update(commentCount);

            articlesWithCommentCount.add(responseDTO);
        }

        return articlesWithCommentCount;
    }

    public ArticleResponseDTO getAndReturnArticleByIdAndSerial(Long id, boolean isUpdate) {

        Article getArticle = getArticleById(id);

        // 게시글 수정 페이지 요청이 아니면, 게시글 조회 요청이면 조회수 증가
        if (!isUpdate) {
            incrementViewCount(getArticle);
        }

        return articleMapper.toDTO(getArticle);
    }

    public ArticleResponseDTO create(ArticleRequestDTO articleRequestDTO) {

        // 게시글 작성자 null check, DB 확인
        Member checkedMember = memberService.getMemberBySerial(articleRequestDTO.getAuthorSerial());

        return saveAndReturnArticle(articleRequestDTO, checkedMember, Optional.empty());

    }

    public ArticleResponseDTO update(Long id, ArticleRequestDTO articleRequestDTO) {

        // 게시글 작성자 null check, DB 확인
        Member checkedMember = memberService.getMemberBySerial(articleRequestDTO.getAuthorSerial());

        return saveAndReturnArticle(articleRequestDTO, checkedMember, Optional.of(id));
    }

    public void delete(Long id) {
        Article article = articleRepository.findById(id).orElseThrow(() ->
                new NoSuchElementException("해당 ID의 게시글이 DB에 존재하지 않습니다."));

        articleRepository.delete(article);
    }

    protected ArticleResponseDTO saveAndReturnArticle(ArticleRequestDTO articleRequestDTO, Member getAuthor, Optional<Long> id) {

        Article article = null;

        // id가 전달되지 않았으면 생성 로직, 전달 됐다면 수정 로직
        if (id.isEmpty()) {
            article = articleMapper.toEntity(articleRequestDTO, getAuthor);
        } else {
            article = getArticleById(id.get());

            article.update(articleRequestDTO.getTitle(), articleRequestDTO.getContent());
        }

        Article savedArticle = articleRepository.save(article);

        return articleMapper.toDTO(savedArticle);
    }

    public Article getArticleById(Long id) {
        return articleRepository.findById(id).orElseThrow(() ->
                new NoSuchElementException("해당 ID의 게시글이 DB에 존재하지 않습니다."));
    }

    // 게시글 조회수 증가 후 DB 저장
    private void incrementViewCount(Article article) {
        article.addViewCount();
        articleRepository.save(article);
    }
}
