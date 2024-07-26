package org.portfolio.spring_1.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CommentRequestDTO {

    private Long articleId;
    private Long parentCommentId;
    private String content;

//    @Builder
//    public CommentRequestDTO(Long articleId, String content) {
//        this.articleId = articleId;
//        this.content = content;
//    }
}
