package org.portfolio.spring_1.repository;

import org.portfolio.spring_1.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<Article, Long> {
}
