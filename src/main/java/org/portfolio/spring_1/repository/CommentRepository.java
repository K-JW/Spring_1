package org.portfolio.spring_1.repository;

import org.portfolio.spring_1.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("SELECT cm FROM Comment cm WHERE cm.article.id = :articleId")
    List<Comment> findAllByArticleId(@Param("articleId") Long articleId);

    @Query("SELECT count(cm) FROM Comment cm WHERE cm.article.id = :articleId")
    int findCommentCountByArticleId(@Param("articleId")Long articleId);

    @Query("SELECT cm FROM Comment cm WHERE cm.id = :parentCommentId")
    Comment findParentCommentById(@Param("parentCommentId") Long parentCommentId);

    @Query("SELECT cm FROM Comment cm WHERE cm != :getComment And cm.parentComment = :getComment ORDER BY cm.id ASC")
    List<Comment> findAllByParentComment(@Param("getComment")Comment getComment);
}
