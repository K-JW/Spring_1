package org.portfolio.spring_1.repository;

import org.portfolio.spring_1.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
