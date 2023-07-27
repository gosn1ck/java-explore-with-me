package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.NewEventDto;
import ru.practicum.ewm.dto.UpdateEventUserRequest;
import ru.practicum.ewm.exception.BadRequestException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.mapper.EventMapper;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.repository.CategoryRepository;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.UserRepository;

import java.util.List;

import static ru.practicum.ewm.model.EventState.PENDING;
import static ru.practicum.ewm.model.EventState.PUBLISHED;

@Service
@RequiredArgsConstructor
public class EventService {
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;

    public List<Event> getAll(Long userId, Integer from, Integer size) {
        var page = PageRequest.of(from / size, size);
        return eventRepository.findAllByInitiatorId(userId, page);
    }

    public Event getUserEvent(Long userId, Long eventId) {
        var event = eventRepository.findFirstByInitiatorIdAndId(userId, eventId);
        if (event == null) {
            throw new NotFoundException("event with id %d not found", eventId);
        }
        return event;
    }

    @Transactional
    public Event update(Long eventId, Long userId, UpdateEventUserRequest dto) {
        var event = eventRepository.findFirstByInitiatorIdAndId(userId, eventId);
        if (event == null) {
            throw new NotFoundException("event with id %d not found", eventId);
        }

        if (event.getState().equals(PUBLISHED)) {
            throw new BadRequestException("Event must not be published");
        }

        eventMapper.updateEntity(event, dto);
        return eventRepository.save(event);
    }

    @Transactional
    public Event add(NewEventDto dto, Long userId) {
        var event = eventMapper.dtoToEntity(dto);
        userRepository.findById(userId).ifPresentOrElse(event::setInitiator,
                () -> {
                    throw new NotFoundException("user with id %d not found", userId);
                });
        categoryRepository.findById(dto.getCategory()).ifPresentOrElse(event::setCategory,
                () -> {
                    throw new NotFoundException("category with id %d not found", dto.getCategory());
                }
        );
        event.setState(PENDING);
        return eventRepository.save(event);
    }

//    private Event getEvent(Long id) {
//        return eventRepository.findById(id)
//                .orElseThrow(() -> new NotFoundException("event with id %d not found", id));
//    }
//
//    private User getUser(Long id) {
//        return userRepository.findById(id)
//                .orElseThrow(() -> new NotFoundException("user with id %d not found", id));
//    }

}
