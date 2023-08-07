package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.CommentDto;
import ru.practicum.ewm.mapper.CommentMapper;
import ru.practicum.ewm.model.Comment;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.repository.CommentRepository;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.UserRepository;
import ru.practicum.ewm.exception.NotFoundException;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final CommentMapper commentMapper;

    @Transactional
    public Comment add(CommentDto dto, Long userId, Long eventId) {
        var user = getUser(userId);
        var event = getEvent(eventId);

//        if (eventRepository.findAllByBookerAndItemAndStatusEqualsAndEndIsBefore(user, item, APPROVED,
//                LocalDateTime.now()).isEmpty()) {
//            throw new BadRequestException("item with id %d not found", itemId);
//        } // todo скорее всего комментарий может оставить только тот кто делал запрос
        // или можно оставить только 1 комментарий ?

        var comment = commentMapper.dtoToEntity(dto);
        return commentRepository.save(comment);
    }

    private User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("user with id %d not found", id));
    }

    private Event getEvent(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("item with id %d not found", id));
    }

}