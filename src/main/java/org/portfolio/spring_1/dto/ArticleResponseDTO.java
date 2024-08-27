package org.portfolio.spring_1.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.portfolio.spring_1.entity.Member;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArticleResponseDTO {

    private Long id;
    private String title;
    private String content;
    private Member authorMember;
    private String createdAt;
    private String modifiedAt;
    private int viewCount;
    private int commentCount;

    public void update(int commentCount) {
        this.commentCount = commentCount;
    }
}
