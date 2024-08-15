package org.portfolio.spring_1.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.portfolio.spring_1.dto.CommentRequestDTO;
import org.portfolio.spring_1.dto.CommentResponseDTO;
import org.portfolio.spring_1.entity.Article;
import org.portfolio.spring_1.entity.Comment;
import org.portfolio.spring_1.entity.Member;
import org.portfolio.spring_1.mapper.CommentMapper;
import org.portfolio.spring_1.repository.CommentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    public CommentResponseDTO create(CommentRequestDTO commentRequestDTO, Article getArticle, Member getMember) {

        Long parentCommentId = commentRequestDTO.getParentCommentId();

        log.info("commentDTO's articleID = {}", commentRequestDTO.getArticleId());
        log.info("commentDTO's parentCommentId = {}", parentCommentId);

//        if (commentRequestDTO == null) {
//            throw new IllegalArgumentException("작성된 댓글이 서버로 전송 실패");
//        }

        Comment getParentComment = commentRepository.findParentCommentById(parentCommentId);

        // getParentComment가 null이 아니면 작성 요청 댓글이 답글, null이면 댓글
        // 답글이면 parentComment인 댓글 entity의 replyComment를 현재 생성된 comment로 변경

        Comment comment = commentMapper.toEntity(commentRequestDTO, getArticle, getMember, getParentComment);

        log.info("commentId = {}", comment.getId());

        Comment savedComment = commentRepository.save(comment);

        log.info("savedComment = {}", savedComment.getId());

        if (getParentComment != null) {
            if (getParentComment.getReplyComment() == null) {
                getParentComment.updateReply(savedComment);
                commentRepository.save(getParentComment);
            }
        }

//        if (getParentComment != null) {
//            getParentComment.update(savedComment);
//            Comment savedParentComment = commentRepository.save(getParentComment);
//            log.info("savedParentComment Id = {}", savedParentComment.getId());
//            return commentMapper.toDTO(savedParentComment);
//        }

        return commentMapper.toDTO(savedComment);
    }

    public List<CommentResponseDTO> getCommentListByArticleId(Long articleId) {
        return commentRepository.findAllByArticleId(articleId).stream()
                .map(commentMapper::toDTO)
                .collect(Collectors.toList());
    }

    public void deleteByArticleId(Long articleId) {
        List<Comment> getCommentList = commentRepository.findAllByArticleId(articleId);

        if (getCommentList.isEmpty()) {
            return;
        }

        commentRepository.deleteAll(getCommentList);

    }

    public int getCommentCountByArticleId(Long articleId) {
        return commentRepository.findCommentCountByArticleId(articleId);
    }

    public CommentResponseDTO update(Long commentId, CommentRequestDTO commentRequest, Member getMember, Article getArticle) {

        Comment getComment = commentRepository.findById(commentId).orElseThrow(() ->
                new NoSuchElementException("해당 ID의 댓글이 DB에 존재하지 않습니다."));

        getComment.update(commentRequest.getContent());

        Comment savedComment = commentRepository.save(getComment);

        return commentMapper.toDTO(savedComment);
    }

    public void deleteByCommentId(Long commentId) {

        // 삭제 요청 댓글 DB 조회
        Comment getComment = commentRepository.findById(commentId).orElseThrow(() ->
                new NoSuchElementException("해당 ID의 댓글이 DB에 존재하지 않습니다."));

        // 조회한 comment가 댓글이면 답글의 parentComment 필드 변경 작업 수행
        if (Objects.equals(getComment.getId(), getComment.getParentComment().getId())) {
            // 삭제 요청 댓글의 답글 DB 조회
            List<Comment> getReplyCommentList = commentRepository.findAllByParentComment(getComment);

            // 답글이 없는 댓글인 경우
            if (getReplyCommentList.isEmpty()) {
                commentRepository.delete(getComment);
                return;
            }

            // 조회된 답글 리스트의 parentComment 를 맨 처음 작성 된 답글 comment 로 변경
            Comment updateParentComment = getReplyCommentList.get(0);

            for (Comment comment : getReplyCommentList) {
                comment.replyUpdate(updateParentComment);
                commentRepository.save(comment);
            }
        }
        // 답글이면 부모 댓글을 조회 해서 부모 댓글의 replyComment를 null로 변경
        else {
            Comment getParentComment = commentRepository.findById(getComment.getParentComment().getId()).orElseThrow(() ->
                    new NoSuchElementException("삭제하려는 답글의 댓글이 DB에 존재하지 않습니다."));

            getParentComment.updateReply(null);
            commentRepository.save(getParentComment);
        }

        commentRepository.delete(getComment);

    }
}
