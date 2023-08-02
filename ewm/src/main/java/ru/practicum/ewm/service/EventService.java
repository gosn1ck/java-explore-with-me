package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.NewEventDto;
import ru.practicum.ewm.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.dto.UpdateEventUserRequest;
import ru.practicum.ewm.exception.BadRequestException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.mapper.EventMapper;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.EventSorts;
import ru.practicum.ewm.model.EventState;
import ru.practicum.ewm.repository.CategoryRepository;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.UserRepository;
import ru.practicum.ewm.specification.EventSpecification;

import java.time.LocalDateTime;
import java.util.*;

import static ru.practicum.ewm.model.EventSorts.VIEWS;
import static ru.practicum.ewm.model.EventState.PENDING;
import static ru.practicum.ewm.model.EventState.PUBLISHED;

@Service
@RequiredArgsConstructor
public class EventService {
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;

    @Transactional(readOnly = true)
    public List<Event> getAllByInitiator(Long userId, Integer from, Integer size) {
        var page = PageRequest.of(from / size, size);
        return eventRepository.findAllByInitiatorId(userId, page);
    }

    @Transactional(readOnly = true)
    public List<Event> getAll(Integer from, Integer size, List<Long> userIds, List<EventState> states,
                              List<Long> categoryIds, LocalDateTime rangeStart, LocalDateTime rangeEnd) {
        var page = PageRequest.of(from / size, size);

        int mode = (userIds != null ? 1 : 0) | (states != null ? 2 : 0) | (categoryIds != null ? 4 : 0);
        var list = new ArrayList<Event>();
        // todo переделать на спеки
        switch (mode) {
            case 0:
                list.addAll(eventRepository.findAll(page).toList());
                break;
            case 1:
                list.addAll(eventRepository.findAllByInitiatorIdIn(userIds, page));
                break;
            case 2:
                list.addAll(eventRepository.findAllByStateIn(states, page));
                break;
            case 3:
                list.addAll(eventRepository.findAllByInitiatorIdInAndStateIn(userIds, states, page));
                break;
            case 4:
                list.addAll(eventRepository.findAllByCategoryIdIn(categoryIds, page));
                break;
            case 5:
                list.addAll(eventRepository.findAllByInitiatorIdInAndCategoryIdIn(userIds, categoryIds, page));
                break;
            case 6:
                list.addAll(eventRepository.findAllByStateInAndCategoryIdIn(states, categoryIds, page));
                break;
            case 7:
                list.addAll(eventRepository.findAllByInitiatorIdInAndStateInAndCategoryIdIn(userIds, states, categoryIds, page));
                break;
        }
        return list;
    }

    @Transactional(readOnly = true)
    public List<Event> getAllPublic(Integer from, Integer size, String text, List<Long> categoryIds, Boolean paid,
                                    LocalDateTime rangeStart, LocalDateTime rangeEnd, Boolean onlyAvailable,
                                    EventSorts sort) {
        PageRequest page = pageByEventSort(from, size, sort);
        var categories = categoryRepository.findAllByIdIn(categoryIds);
        var list = new ArrayList<Event>();

        if (rangeStart == null || rangeEnd == null) {
            list.addAll(eventRepository.findAll(EventSpecification.publicSearchWithoutRange(text, categories, paid,
                    onlyAvailable), page).toList());
        } else {
            list.addAll(eventRepository.findAll(EventSpecification.publicSearchWithRange(text,
                    categories, paid, onlyAvailable, rangeStart, rangeEnd), page).toList());
        }
        return list;
    }

    @Transactional(readOnly = true)
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
    public Event updateByAdmin(Long eventId, UpdateEventAdminRequest dto) {
        var event = getEvent(eventId);
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

    @Transactional(readOnly = true)
    public Set<Event> getEvents(Set<Long> ids) {
        if (ids == null) {
            return new HashSet<>();
        }
        var events = new HashSet<Event>();
        for (Long id : ids) {
            events.add(getEvent(id));
        }
        return events;
    }

    public Optional<Event> findById(Long id) {
        return Optional.of(getEvent(id));
    }

    private Event getEvent(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("event with id %d not found", id));
    }

    private PageRequest pageByEventSort(Integer from, Integer size, EventSorts sort) {
        if (sort.equals(VIEWS)) {
            return PageRequest.of(from / size, size, Sort.by("views"));
        } else {
            return PageRequest.of(from / size, size, Sort.by("eventDate"));
        }
    }

//    private User getUser(Long id) {
//        return userRepository.findById(id)
//                .orElseThrow(() -> new NotFoundException("user with id %d not found", id));
//    }

}
