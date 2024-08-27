package org.portfolio.spring_1.mapper;

import org.portfolio.spring_1.dto.ArticleRequestDTO;
import org.portfolio.spring_1.dto.ArticleResponseDTO;
import org.portfolio.spring_1.entity.Article;
import org.portfolio.spring_1.entity.Member;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
public class ArticleMapper {

    public Article toEntity(ArticleRequestDTO articleRequestDTO, Member author) {
        return Article.builder()
                .title(articleRequestDTO.getTitle())
                .content(articleRequestDTO.getContent())
                .author(author)
                .build();
    }

    public ArticleResponseDTO toDTO(Article article) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy-MM-dd HH:mm:ss");
        String formattedCreatedAt = article.getCreatedAt().format(formatter);
        String formattedModifiedAt = article.getModifiedAt().format(formatter);

        return ArticleResponseDTO.builder()
                .id(article.getId())
                .title(article.getTitle())
                .content(article.getContent())
                .authorMember(article.getAuthor())
                .createdAt(formattedCreatedAt)
                .modifiedAt(formattedModifiedAt)
                .viewCount(article.getViewCount())
                .build();
    }

}
