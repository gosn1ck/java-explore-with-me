package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.Request;
import ru.practicum.ewm.model.RequestStatus;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {

    Request findFirstByRequesterIdAndEventId(Long requesterId, Long eventId);

    List<Request> findAllByEventIdAndStatus(Long id, RequestStatus status);

    List<Request> findAllByRequesterId(Long requesterId);

    Optional<Integer> countByEventAndStatus(Event event, RequestStatus status);

    List<Request> findAllByEventInAndStatus(List<Event> events, RequestStatus status);

}
