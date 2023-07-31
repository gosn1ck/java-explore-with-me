package ru.practicum.ewm.service;

import com.sun.jdi.request.EventRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.exception.ClientErrorException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.model.*;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.RequestRepository;
import ru.practicum.ewm.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.ewm.model.EventState.PUBLISHED;
import static ru.practicum.ewm.model.RequestStatus.*;

@Service
@RequiredArgsConstructor
public class RequestService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Transactional
    public Request add(Long userId, Long eventId) {
        var user = getUser(userId);
        var event = getEvent(eventId);

        var request = requestRepository.findFirstByRequesterIdAndEventId(userId, eventId);
        if (request != null) {
            throw new ClientErrorException("impossible to make double request");
        }
        if (!event.getState().equals(PUBLISHED)) {
            throw new ClientErrorException("impossible request to event %d", eventId);
        }

        if (event.getInitiator().getId().equals(user.getId())) {
            throw new ClientErrorException("impossible request to event %d", eventId);
        }

        var confirmedRequests = requestRepository.findAllByEventIdAndStatus(eventId, CONFIRMED.toString());
        if (confirmedRequests.size() == event.getParticipantLimit()) {
            throw new ClientErrorException("impossible request to event. participant limit");
        }

        request = Request.builder()
                .event(event)
                .requester(user)
                .created(LocalDateTime.now())
                .build();

        if (event.getRequestModeration() && event.getParticipantLimit() != 0) {
            request.setStatus(PENDING);
        } else {
            request.setStatus(CONFIRMED);
        }

        return requestRepository.save(request);
    }

    @Transactional(readOnly = true)
    public List<Request> getAll(Long userId) {
        var user = getUser(userId);
        return requestRepository.findAllByRequesterId(user.getId());
    }

    @Transactional
    public Request cancel(Long userId, Long requestId) {
        var user = getUser(userId);
        var request = getRequest(requestId);

        if (!request.getRequester().getId().equals(user.getId())) {
            throw new ClientErrorException("impossible cancel request  %d", requestId);
        }

        request.setStatus(CANCELED);
        return request;
    }

    private Request getRequest(Long id) {
        return requestRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("request with id %d not found", id));
    }

    private Event getEvent(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("event with id %d not found", id));
    }

    private User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("user with id %d not found", id));
    }

}
