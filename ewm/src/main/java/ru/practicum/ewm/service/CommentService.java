package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.CommentDto;
import ru.practicum.ewm.dto.CommentLikeDto;
import ru.practicum.ewm.dto.CommentShortResponse;
import ru.practicum.ewm.exception.ClientErrorException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.mapper.CommentLikeMapper;
import ru.practicum.ewm.mapper.CommentMapper;
import ru.practicum.ewm.model.*;
import ru.practicum.ewm.repository.CommentLikeRepository;
import ru.practicum.ewm.repository.CommentRepository;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.UserRepository;

import java.util.List;

import static ru.practicum.ewm.model.CommentState.*;
import static ru.practicum.ewm.model.EventState.PUBLISHED;
import static ru.practicum.ewm.model.LikeType.DISLIKE;
import static ru.practicum.ewm.model.LikeType.LIKE;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final CommentMapper commentMapper;
    private final CommentLikeMapper commentLikeMapper;

    public Comment add(CommentDto dto, Long userId, Long eventId) {
        var comment = commentMapper.dtoToEntity(dto);
        userRepository.findById(userId).ifPresentOrElse(comment::setUser,
                () -> {
                    throw new NotFoundException("user with id %d not found", userId);
                });
        eventRepository.findById(eventId).ifPresentOrElse(comment::setEvent,
                () -> {
                    throw new NotFoundException("event with id %d not found", eventId);
                }
        );

        checkNewComment(comment);

        comment.setState(DRAFT);
        return commentRepository.save(comment);
    }

    public Comment update(CommentDto dto, Long userId, Long eventId, Long commentId) {
        var comment = getComment(commentId);
        checkFoundedComment(userId, eventId, comment);
        if (comment.getState().equals(MODERATED)) {
            throw new ClientErrorException("comment could not be updated in moderated state");
        }
        commentMapper.updateEntity(comment, dto);
        return commentRepository.save(comment);
    }

    @Transactional(readOnly = true)
    public List<Comment> findAllByEventId(Long id, Integer from, Integer size) {
        getEvent(id);
        var page = PageRequest.of(from / size, size, Sort.by("created"));
        return commentRepository.findAllByEventIdAndState(id, MODERATED, page);
    }

    @Transactional(readOnly = true)
    public List<Comment> findAllByEventId(Long id) {
        getEvent(id);
        return commentRepository.findAllByEventIdAndState(id, MODERATED);
    }

    @Transactional(readOnly = true)
    public Comment findCommentById(Long userId, Long eventId, Long commentId) {
        getUser(userId);
        getEvent(eventId);
        var comment = getComment(commentId);

        checkFoundedComment(userId, eventId, comment);

        return comment;
    }

    public void deleteById(Long id) {
        getComment(id);
        commentRepository.deleteById(id);
    }

    public Comment approve(Long id) {
        var comment = getComment(id);
        if (!comment.getState().equals(DRAFT)) {
            throw new ClientErrorException("comment with id %d could be approve only in draft state", id);
        }
        comment.setState(MODERATED);
        commentRepository.save(comment);
        return comment;
    }

    public Comment reject(Long id) {
        var comment = getComment(id);
        if (!comment.getState().equals(DRAFT)) {
            throw new ClientErrorException("comment with id %d could be reject only in draft state", id);
        }
        comment.setState(REJECTED);
        commentRepository.save(comment);
        return comment;
    }

    public CommentLike addLike(CommentLikeDto dto, Long userId, Long commentId) {
        var like = commentLikeMapper.dtoToEntity(dto);
        userRepository.findById(userId).ifPresentOrElse(like::setUser,
                () -> {
                    throw new NotFoundException("user with id %d not found", userId);
                });
        commentRepository.findById(commentId).ifPresentOrElse(like::setComment,
                () -> {
                    throw new NotFoundException("comment with id %d not found", commentId);
                }
        );

        checkNewLike(like, userId, commentId);

        return commentLikeRepository.save(like);
    }

    public CommentShortResponse findLikes(CommentShortResponse comment) {
        var likes = commentLikeRepository.findAllByCommentId(comment.getId());
        comment.setLikes(likes.stream().filter(like -> like.getType().equals(LIKE)).count());
        comment.setDislikes(likes.stream().filter(like -> like.getType().equals(DISLIKE)).count());
        return comment;
    }

    private void checkNewComment(Comment comment) {
        if (!comment.getEvent().getState().equals(PUBLISHED)) {
            throw new ClientErrorException("comments are available only for published event");
        }
    }

    private void checkNewLike(CommentLike like, Long userId, Long commentId) {
        if (!like.getComment().getState().equals(MODERATED)) {
            throw new ClientErrorException("like or dislike are available only for moderated comment");
        }

        var likes = commentLikeRepository.findAllByUserIdAndCommentId(userId, commentId);
        if (!likes.isEmpty()) {
            throw new ClientErrorException("like or dislike are done already to comment id %d by user is %d",
                    commentId, userId);
        }

    }

    private void checkFoundedComment(Long userId, Long eventId, Comment comment) {
        if (!comment.getUser().getId().equals(userId)) {
            throw new ClientErrorException("comment with id %d do not belong to user id %d",
                    comment.getId(), userId);
        }
        if (!comment.getEvent().getId().equals(eventId)) {
            throw new ClientErrorException("comment with id %d do not belong to event id %d",
                    comment.getId(), eventId);
        }
    }

    private Event getEvent(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("event with id %d not found", id));
    }

    private User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("user with id %d not found", id));
    }

    private Comment getComment(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("comment with id %d not found", id));
    }
}