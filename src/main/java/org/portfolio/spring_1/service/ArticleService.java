package org.portfolio.spring_1.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.portfolio.spring_1.dto.ArticleRequestDTO;
import org.portfolio.spring_1.dto.ArticleResponseDTO;
import org.portfolio.spring_1.entity.Article;
import org.portfolio.spring_1.entity.Member;
import org.portfolio.spring_1.mapper.ArticleMapper;
import org.portfolio.spring_1.repository.ArticleRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

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

    public Page<ArticleResponseDTO> getAll(Pageable pageable) {

        int pageNumber = pageable.getPageNumber()-1;

        int pageLimit = 3;

        Page<Article> pagingArticles = articleRepository.findAll(PageRequest.of(pageNumber, pageLimit, Sort.by(Sort.Direction.DESC, "id")));

        // 총 게시글 수, 페이지 수 등 페이징 정보 출력
        getPagingArticlesInfo(pagingArticles);

        if (pagingArticles.isEmpty()) {
            return Page.empty();
        }

        Page<ArticleResponseDTO> pagingDTOs = pagingArticles.map(articleMapper::toDTO);

        for (ArticleResponseDTO pagingDTO : pagingDTOs) {
            Long articleId = pagingDTO.getId();

            int commentCount = commentService.getCommentCountByArticleId(articleId);

            pagingDTO.update(commentCount);
        }

        return pagingDTOs;

    }

    public void getPagingArticlesInfo(Page<Article> pagingArticles) {
        System.out.println(pagingArticles.getContent());
        System.out.println(pagingArticles.getTotalElements());
        System.out.println(pagingArticles.getNumber());
        System.out.println(pagingArticles.getTotalPages());
        System.out.println(pagingArticles.getSize());
        System.out.println(pagingArticles.hasPrevious());
        System.out.println(pagingArticles.isFirst());
        System.out.println(pagingArticles.isLast());
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
