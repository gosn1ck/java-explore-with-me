package ru.practicum.ewm.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.model.Comment;
import ru.practicum.ewm.model.CommentState;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByEventIdAndState(Long id, CommentState state, Pageable page);

    List<Comment> findAllByEventIdAndState(Long id, CommentState state);

}