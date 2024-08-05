package org.portfolio.spring_1.entity;

import jakarta.persistence.PrePersist;


public class CommentListener {

    @PrePersist
    public void prePersist(Comment comment) {
        if (comment.getParentComment() == null) {
            comment.setParentComment(comment);
        }
    }

}
