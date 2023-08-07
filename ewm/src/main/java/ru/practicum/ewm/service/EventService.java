package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.*;
import ru.practicum.ewm.exception.ClientErrorException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.mapper.EventMapper;
import ru.practicum.ewm.mapper.RequestMapper;
import ru.practicum.ewm.model.*;
import ru.practicum.ewm.repository.CategoryRepository;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.RequestRepository;
import ru.practicum.ewm.repository.UserRepository;
import ru.practicum.ewm.specification.EventSpecification;
import ru.practicum.stats.client.HitClient;
import ru.practicum.stats.client.StatsClient;
import ru.practicum.stats.dto.HitDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.ewm.model.EventSorts.VIEWS;
import static ru.practicum.ewm.model.EventState.*;
import static ru.practicum.ewm.model.RequestStatus.CONFIRMED;
import static ru.practicum.ewm.util.Constants.APP_NAME;
import static ru.practicum.ewm.util.Constants.FORMATTER;

@Service
@RequiredArgsConstructor
public class EventService {
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final EventMapper eventMapper;
    private final RequestMapper requestMapper;
    private final HitClient hitClient;
    private final StatsClient statsClient;

    public List<Event> getAllByInitiator(Long userId, Integer from, Integer size) {
        var page = PageRequest.of(from / size, size);
        return eventRepository.findAllByInitiatorId(userId, page);
    }

    @Transactional(readOnly = true)
    public List<Event> getAllAdmin(Integer from, Integer size, List<Long> userIds, List<EventState> states,
                                   List<Long> categoryIds, LocalDateTime rangeStart, LocalDateTime rangeEnd) {
        var page = PageRequest.of(from / size, size);
        var list = new ArrayList<Event>();

        var users = userIds == null ? new ArrayList<User>() : userRepository.findAllById(userIds);
        var categories = categoryIds == null ? new ArrayList<Category>() : categoryRepository.findAllById(categoryIds);
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
                                    EventSorts sort, HttpServletRequest request) {
        PageRequest page = pageByEventSort(from, size, sort);
        var list = new ArrayList<Event>();

        var categories = categoryIds == null ? new ArrayList<Category>() : categoryRepository.findAllById(categoryIds);
        if (rangeStart == null || rangeEnd == null) {
            list.addAll(eventRepository.findAll(EventSpecification.publicSearchWithoutRange(
                    text, categories, paid, onlyAvailable), page).toList());
        } else {
            list.addAll(eventRepository.findAll(EventSpecification.publicSearchWithRange(
                    text, categories, paid, onlyAvailable, rangeStart, rangeEnd), page).toList());
        }
        saveHit(request);
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
            throw new ClientErrorException("Event must not be published");
        }

        updateState(event, dto.getStateAction());

        eventMapper.updateEntity(event, dto);
        return eventRepository.save(event);
    }

    @Transactional
    public Event updateByAdmin(Long eventId, UpdateEventAdminRequest dto) {
        var event = getEvent(eventId);

        updateState(event, dto.getStateAction());

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

    @Transactional(readOnly = true)
    public Event findById(Long id, HttpServletRequest request) {
        var event = getEvent(id);
        if (!event.getState().equals(PUBLISHED)) {
            throw new NotFoundException("event with id %d not found", id);
        }
        saveHit(request);
        return event;
    }

    @Transactional(readOnly = true)
    public List<Request> getRequests(Long userId, Long eventId) {
        var user = getUser(userId);
        var event = getEvent(eventId);
        checkInitiator(user, event);

        return requestRepository.findAllByEvent(event);

    }

    @Transactional
    public EventRequestStatusUpdateResult updateParticipationRequest(
            Long userId, Long eventId, EventRequestStatusUpdateRequest dto) {
        var user = getUser(userId);
        var event = getEvent(eventId);
        checkInitiator(user, event);

        List<Request> requests = requestRepository.findAllByIdIn(dto.getRequestIds());
        checkEventLimit(event);

        if (requests.isEmpty()) return EventRequestStatusUpdateResult.builder().build();

        requests = requests.stream().filter(r -> r.getStatus().equals(RequestStatus.PENDING)).collect(Collectors.toList());

        switch (dto.getStatus()) {
            case REJECTED:
                rejectRequests(requests);
                break;
            case CONFIRMED:
                filterAndProcessRequests(requests, event);
                break;
        }

        var dtos = requests.stream().map(requestMapper::entityToParticipationRequest).collect(Collectors.toList());

        return EventRequestStatusUpdateResult.builder()
                .rejectedRequests(dtos.stream().filter(r -> r.getStatus().equals(RequestStatus.REJECTED)).collect(Collectors.toList()))
                .confirmedRequests(dtos.stream().filter(r -> r.getStatus().equals(CONFIRMED)).collect(Collectors.toList()))
                .build();
    }

    public Integer getViews(Long eventId) {
        var uri = "/events/" + eventId;
        var response = statsClient.getStats(
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1),
                List.of(uri), true);
        return response.getBody().stream().filter(r -> r.getUri().equals(uri)).findFirst().get().getHits();
    }

    private Event getEvent(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("event with id %d not found", id));
    }

    private User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("user with id %d not found", id));
    }

    private void checkInitiator(User user, Event event) {
        if (!event.getInitiator().getId().equals(user.getId())) {
            throw new ClientErrorException("not valid initiator");
        }
    }

    private void checkEventLimit(Event event) {
        var confirmedRequests = requestRepository.findAllByEventIdAndStatus(event.getId(), CONFIRMED);
        if (event.getParticipantLimit() - confirmedRequests.size() < 1) {
            throw new ClientErrorException("impossible request to event. participant limit");
        }
    }

    private void filterAndProcessRequests(List<Request> requests, Event event) {
        if (event.getParticipantLimit() == 0) {
            confirmRequests(requests);
        } else {
            var confirmedRequests = requestRepository.findAllByEventIdAndStatus(event.getId(), CONFIRMED);
            int delta = event.getParticipantLimit() - confirmedRequests.size();
            if (requests.size() <= delta) {
                confirmRequests(requests);
            } else if (delta > 0) {
                confirmRequests(requests.subList(0, delta - 1));
                rejectRequests(requests.subList(delta, requests.size()));
            } else {
                rejectRequests(requests);
            }
        }
    }

    private void confirmRequests(List<Request> requests) {
        requests.forEach(request -> request.setStatus(RequestStatus.CONFIRMED));
        requestRepository.saveAll(requests);
    }

    private void rejectRequests(List<Request> requests) {
        requests.forEach(request -> request.setStatus(RequestStatus.REJECTED));
        requestRepository.saveAll(requests);
    }

    private PageRequest pageByEventSort(Integer from, Integer size, EventSorts sort) {
        if (sort.equals(VIEWS)) {
            return PageRequest.of(from / size, size, Sort.by("views"));
        } else {
            return PageRequest.of(from / size, size, Sort.by("eventDate"));
        }
    }

    private void updateState(Event event, StateActions action) {
        if (action == null) {
            return;
        }
        switch (action) {
            case CANCEL_REVIEW:
                event.setState(CANCELED);
                break;
            case PUBLISH_EVENT:
                publishEvent(event);
                break;
            case REJECT_EVENT:
                rejectEvent(event);
                break;
            case SEND_TO_REVIEW:
                event.setState(PENDING);
                break;
        }
    }

    private void publishEvent(Event event) {
        if (event.getEventDate().isAfter(LocalDateTime.now().plusHours(1))
                && event.getState().equals(PENDING)) {
            event.setState(PUBLISHED);
            event.setPublishedOn(LocalDateTime.now());
        } else if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
            throw new ClientErrorException("impossible to publish event");
        } else if (!event.getState().equals(PENDING)) {
            throw new ClientErrorException("impossible to publish event");
        }
    }

    public void rejectEvent(Event event) {
        if (!event.getState().equals(PUBLISHED)) {
            event.setState(CANCELED);
        } else {
            throw new ClientErrorException("impossible to reject event");
        }
    }

    private void saveHit(HttpServletRequest request) {
        var ip = request.getRemoteAddr().equals("0:0:0:0:0:0:0:1") ? "127.0.0.1" : request.getRemoteAddr();
        var dto = HitDto.builder()
                .app(APP_NAME)
                .uri(request.getRequestURI())
                .ip(ip)
                .timestamp(LocalDateTime.now().format(FORMATTER))
                .build();
        hitClient.add(dto);
    }

}