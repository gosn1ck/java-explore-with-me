package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.model.CommentLike;

import java.util.List;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {

    List<CommentLike> findAllByUserIdAndCommentId(Long userId, Long commentId);

    List<CommentLike> findAllByCommentId(Long id);

}