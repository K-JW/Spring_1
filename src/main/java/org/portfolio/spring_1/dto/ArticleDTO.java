package org.portfolio.spring_1.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.portfolio.spring_1.entity.Article;
import org.portfolio.spring_1.entity.Member;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ArticleDTO {

    private String title;
    private String content;
    private String author;

    public Article toEntity(ArticleDTO articleDTO, Member author) {
        return Article.builder()
                .title(articleDTO.getTitle())
                .content(articleDTO.getContent())
                .author(author)
                .build();
    }
}
