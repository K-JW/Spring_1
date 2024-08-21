package org.portfolio.spring_1.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@EntityListeners(CommentListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @Setter
    @ManyToOne
    @JoinColumn(name = "parent_comment_id", referencedColumnName = "comment_id")
    private Comment parentComment;

    @ManyToOne
    @JoinColumn(name = "article_id")
    private Article article;

    @ManyToOne
    @JoinColumn(name = "name", referencedColumnName = "serial")
    private Member author;

    @Column(name = "content")
    private String content;

    @OneToOne
    @JoinColumn(name = "reply_comment_id", referencedColumnName = "comment_id", nullable = true)
    private Comment replyComment;

    @Builder
    public Comment(Article article, Comment parentComment, Member author, String content) {
        this.article = article;
        this.parentComment = parentComment;
        this.author = author;
        this.content = content;
    }

    public void update(String content) {
        this.content = content;
    }

    public void replyUpdate(Comment updateParentComment) {
        this.parentComment = updateParentComment;
    }

    public void updateReply(Comment savedComment) {
        this.replyComment = savedComment;
    }
}
