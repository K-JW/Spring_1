package org.portfolio.spring_1.dto;

import lombok.Builder;
import lombok.Getter;
import org.portfolio.spring_1.entity.Comment;

@Getter
@Builder
public class CommentResponseDTO {

    private Long articleId;
    private Long commentId;
    private Comment parentComment;
    private Comment replyComment;
    private String authorName;
    private String createdAt;
    private String modifiedAt;
    private String content;
}
