package ru.practicum.ewm.controller.publ;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.practicum.ewm.dto.CommentLikeDto;
import ru.practicum.ewm.dto.CommentLikeResponse;
import ru.practicum.ewm.mapper.CommentLikeMapper;
import ru.practicum.ewm.service.CommentService;

import javax.validation.Valid;
import java.net.URI;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("users/{userId}/comments")
public class UserCommentsController {

    private final CommentService commentService;
    private final CommentLikeMapper commentLikeMapper;

    @PostMapping("/{commentId}/like")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<CommentLikeResponse> addCommentLike(
            @PathVariable("userId") Long userId,
            @PathVariable("commentId") Long commentId,
            @Valid @RequestBody CommentLikeDto dto) {
        log.info("New like comment registration {}, from userId {} to comment {}", dto, userId, commentId);
        var savedLike = commentService.addLike(dto, userId, commentId);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedLike.getId()).toUri();
        return ResponseEntity.created(location).body(commentLikeMapper.entityToResponse(savedLike));
    }

}