package ru.practicum.ewm.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.EventState;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {

    List<Event> findAllByInitiatorId(Long userId, Pageable page);

    Event findFirstByInitiatorIdAndId(Long userId, Long id);

    List<Event> findAllByInitiatorIdIn(List<Long> initiatorIds, Pageable page);

    List<Event> findAllByStateIn(List<EventState> states, Pageable page);

    List<Event> findAllByInitiatorIdInAndStateIn(List<Long> initiatorIds, List<EventState> states, Pageable page);

    List<Event> findAllByCategoryIdIn(List<Long> categoryIds, Pageable page);

    List<Event> findAllByInitiatorIdInAndCategoryIdIn(List<Long> initiatorIds, List<Long> categoryIds, Pageable page);

    List<Event> findAllByStateInAndCategoryIdIn(List<EventState> states, List<Long> categoryIds, Pageable page);

    List<Event> findAllByInitiatorIdInAndStateInAndCategoryIdIn(
            List<Long> initiatorIds, List<EventState> states, List<Long> categoryIds, Pageable page);

}
