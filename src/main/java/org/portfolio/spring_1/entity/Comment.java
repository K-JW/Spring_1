package org.portfolio.spring_1.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "article_id")
    private Article article;

    @ManyToOne
    @JoinColumn(name = "name", referencedColumnName = "serial")
    private Member author;

    @Column(name = "content")
    private String content;

    @Builder
    public Comment(Article article, Member author, String content) {
        this.article = article;
        this.author = author;
        this.content = content;
    }
}
