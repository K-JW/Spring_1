package org.portfolio.spring_1.entity;

import jakarta.persistence.PrePersist;

import java.util.Objects;

public class CommentListener {

    @PrePersist
    public void prePersist(Comment comment) {
        if (comment.getParentComment() == null) {
            comment.setParentComment(comment);
            comment.setReplyComment(comment);
        }

        if (!Objects.equals(comment.getId(), comment.getParentComment().getId())) {
            comment.setReplyComment(comment);
        }
    }
}
