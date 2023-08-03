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
import ru.practicum.stats.client.HitClient;
import ru.practicum.stats.dto.HitDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static ru.practicum.ewm.model.EventSorts.VIEWS;
import static ru.practicum.ewm.model.EventState.PENDING;
import static ru.practicum.ewm.model.EventState.PUBLISHED;
import static ru.practicum.ewm.util.Constants.APP_NAME;
import static ru.practicum.ewm.util.Constants.DATE_FORMAT;

@Service
@RequiredArgsConstructor
public class EventService {
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final HitClient hitClient;
    private final DateTimeFormatter Formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);

    @Transactional(readOnly = true)
    public List<Event> getAllByInitiator(Long userId, Integer from, Integer size) {
        var page = PageRequest.of(from / size, size);
        return eventRepository.findAllByInitiatorId(userId, page);
    }

    @Transactional(readOnly = true)
    public List<Event> getAllAdmin(Integer from, Integer size, List<Long> userIds, List<EventState> states,
                                   List<Long> categoryIds, LocalDateTime rangeStart, LocalDateTime rangeEnd) {
        var page = PageRequest.of(from / size, size);
        var list = new ArrayList<Event>();

        var users = userRepository.findAllById(userIds);
        var categories = categoryRepository.findAllById(categoryIds);
        if (rangeStart == null || rangeEnd == null) {
            list.addAll(eventRepository.findAll(EventSpecification.adminSearchWithoutDate(
                    users, categories, states), page).toList());
        } else {
            list.addAll(eventRepository.findAll(EventSpecification.adminSearchWithDate(
                    users, categories, states, rangeStart, rangeEnd), page).toList());
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
            list.addAll(eventRepository.findAll(EventSpecification.publicSearchWithoutRange(
                    text, categories, paid, onlyAvailable), page).toList());
        } else {
            list.addAll(eventRepository.findAll(EventSpecification.publicSearchWithRange(
                    text, categories, paid, onlyAvailable, rangeStart, rangeEnd), page).toList());
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

        if (event.getState().equals(EventState.CANCELED)) {
            event.setState(EventState.PENDING);
        }

        eventMapper.updateEntity(event, dto);
        return eventRepository.save(event);
    }

    @Transactional
    public Event updateByAdmin(Long eventId, UpdateEventAdminRequest dto) {
        var event = getEvent(eventId);

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

    public Event findById(Long id, HttpServletRequest request) {
        var event = getEvent(id);
        saveHit(request);
        return event;
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

    private void saveHit(HttpServletRequest request) {
        var dto = HitDto.builder()
                .app(APP_NAME)
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now().format(Formatter))
                .build();
        hitClient.add(dto);
    }

}
