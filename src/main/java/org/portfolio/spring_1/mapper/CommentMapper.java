package org.portfolio.spring_1.mapper;

import org.portfolio.spring_1.dto.ArticleRequestDTO;
import org.portfolio.spring_1.dto.CommentRequestDTO;
import org.portfolio.spring_1.dto.CommentResponseDTO;
import org.portfolio.spring_1.entity.Article;
import org.portfolio.spring_1.entity.Comment;
import org.portfolio.spring_1.entity.Member;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
public class CommentMapper {

    public Comment toEntity(CommentRequestDTO commentRequestDTO, Article article, Member author) {

        return Comment.builder()
                .article(article)
                .author(author)
                .content(commentRequestDTO.getContent())
                .build();
    }

    public CommentResponseDTO toDTO(Comment comment) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy-MM-dd HH:mm:ss");
        String formattedCreatedAt = comment.getCreatedAt().format(formatter);
        String formattedModifiedAt = comment.getModifiedAt().format(formatter);

        return CommentResponseDTO.builder()
                .authorName(comment.getAuthor().getName())
                .content(comment.getContent())
                .createdAt(formattedCreatedAt)
                .modifiedAt(formattedModifiedAt)
                .build();
    }

}
