package ru.practicum.ewm.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.EventFullDto;
import ru.practicum.ewm.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.exception.BadRequestException;
import ru.practicum.ewm.mapper.EventMapper;
import ru.practicum.ewm.model.EventState;
import ru.practicum.ewm.service.EventService;
import ru.practicum.ewm.service.RequestService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.ewm.util.Constants.DATE_FORMAT;
import static ru.practicum.ewm.util.Constants.FORMATTER;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/events")
public class AdminEventsController {

    private final EventService eventService;
    private final RequestService requestService;
    private final EventMapper eventMapper;

    @GetMapping
    public ResponseEntity<List<EventFullDto>> getAll(
            @RequestParam(value = "from", defaultValue = "0")
            @Min(value = 0, message = "minimum value for from param is 0") Integer from,
            @RequestParam(value = "size", defaultValue = "10")
            @Min(value = 1, message = "minimum value for size param is 1") Integer size,
            @RequestParam(value = "users", required = false) List<Long> userIds,
            @RequestParam(value = "states", required = false) List<EventState> states,
            @RequestParam(value = "categories", required = false) List<Long> categoryIds,
            @RequestParam(value = "rangeStart", required = false)
            @DateTimeFormat(pattern = DATE_FORMAT) @Valid LocalDateTime rangeStart,
            @RequestParam(value = "rangeEnd", required = false)
            @DateTimeFormat(pattern = DATE_FORMAT) @Valid LocalDateTime rangeEnd
        ) {
        log.info("Get all events, from {}, size {}, users {}, states {}, categories {}, rangeStart {}, rangeEnd {}",
                from, size, userIds, states, categoryIds, rangeStart, rangeEnd);

        if (rangeStart != null && rangeEnd != null && rangeEnd.isBefore(rangeStart)) {
            throw new BadRequestException("range end is before range start");
        }

        var events = eventService.getAllAdmin(from, size, userIds, states, categoryIds, rangeStart, rangeEnd);
        var eventRequests = requestService.requestsByEvents(events);
        var fullDtos = events.stream().map(event -> {
            var fullDto = eventMapper.entityToEventFullDto(event);
            var requests = eventRequests.getOrDefault(event, new ArrayList<>());
            fullDto.setConfirmedRequests(requests.size());
            return fullDto;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(fullDtos);
    }

    @PatchMapping(consumes = "application/json", path = "/{eventId}")
    public ResponseEntity<EventFullDto> update(
            @RequestBody @Valid UpdateEventAdminRequest dto,
            @PathVariable("eventId") Long eventId) {
        log.info("Update event {} with id {}", dto, eventId);

        if (dto.getEventDate() != null) {
            var date = LocalDateTime.parse(dto.getEventDate(), FORMATTER);
            if (date.isBefore(LocalDateTime.now().plusHours(1))) {
                throw new BadRequestException("impossible to update event");
            }
        }

        var event = eventService.updateByAdmin(eventId, dto);
        var fullDto = eventMapper.entityToEventFullDto(event);
        fullDto.setConfirmedRequests(requestService.requestsByEvent(event));
        return ResponseEntity.ok(fullDto);
    }

}