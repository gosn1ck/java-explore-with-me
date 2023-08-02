package ru.practicum.ewm.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.*;
import ru.practicum.ewm.exception.BadRequestException;
import ru.practicum.ewm.mapper.EventMapper;
import ru.practicum.ewm.model.EventState;
import ru.practicum.ewm.service.EventService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.ewm.util.Constants.DATE_FORMAT;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/events")
public class AdminEventsController {

    private final EventService eventService;
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

        if (rangeStart.isAfter(rangeEnd)) {
            throw new BadRequestException("rangeStart parameter should be before rangeEnd parameter");
        }

        var events = eventService.getAll(from, size, userIds, states, categoryIds, rangeStart, rangeEnd);
        return ResponseEntity.ok(
                events.stream().map(eventMapper::entityToEventFullDto).collect(Collectors.toList()));
    }

    @PatchMapping(consumes = "application/json", path = "/{eventId}")
    public ResponseEntity<EventFullDto> update(@RequestBody UpdateEventAdminRequest dto,
                                                   @PathVariable("eventId") Long eventId) {
        log.info("Update event {} with id {}", dto, eventId);
        var event = eventService.updateByAdmin(eventId, dto);
        return ResponseEntity.ok(eventMapper.entityToEventFullDto(event));
    }

}
