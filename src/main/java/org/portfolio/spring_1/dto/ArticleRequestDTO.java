package org.portfolio.spring_1.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.portfolio.spring_1.entity.Article;
import org.portfolio.spring_1.entity.Member;

@Getter
@Builder
public class ArticleRequestDTO {

    private String title;
    private String content;
    private String authorSerial;

}
