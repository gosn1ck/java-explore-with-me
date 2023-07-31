package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.model.Request;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {

    Request findFirstByRequesterIdAndEventId(Long requesterId, Long eventId);

    List<Request> findAllByEventIdAndStatus(Long eventId, String status);

    List<Request> findAllByRequesterId(Long requesterId);

}
