package ru.practicum.ewm.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.CommentResponse;
import ru.practicum.ewm.mapper.CommentMapper;
import ru.practicum.ewm.service.CommentService;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/comments")
public class AdminCommentController {

    private final CommentService commentService;
    private final CommentMapper commentMapper;

    @PatchMapping("/{commentId}/approve")
    public ResponseEntity<CommentResponse> approve(
            @PathVariable("commentId") Long id) {
        log.info("Approve commentId {}", id);
        return ResponseEntity.ok(
                commentMapper.entityToResponse(commentService.approve(id)));
    }

    @PatchMapping("/{commentId}/reject")
    public ResponseEntity<CommentResponse> reject(
            @PathVariable("commentId") Long id) {
        log.info("Approve commentId {}", id);
        return ResponseEntity.ok(
                commentMapper.entityToResponse(commentService.reject(id)));
    }

    @DeleteMapping("{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("commentId") Long id) {
        log.info("Remove comment with id: {}", id);
        commentService.deleteById(id);
    }

}