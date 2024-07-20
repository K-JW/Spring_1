package org.portfolio.spring_1.controller.api;

import lombok.RequiredArgsConstructor;
import org.portfolio.spring_1.dto.CommentRequestDTO;
import org.portfolio.spring_1.dto.CommentResponseDTO;
import org.portfolio.spring_1.service.CommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comments")
public class CommentApiController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentResponseDTO> createComment(@RequestBody CommentRequestDTO commentRequestDTO) {

        CommentResponseDTO responseComment = commentService.create(commentRequestDTO);

        return ResponseEntity.ok().body(responseComment);
    }


}