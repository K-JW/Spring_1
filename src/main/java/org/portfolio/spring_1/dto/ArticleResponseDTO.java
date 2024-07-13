package org.portfolio.spring_1.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArticleResponseDTO {

    private Long id;
    private String title;
    private String content;
    private String authorName;
    private String createdAt;
}
