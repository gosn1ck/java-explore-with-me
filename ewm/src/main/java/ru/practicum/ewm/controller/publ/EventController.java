package ru.practicum.ewm.controller.publ;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.EventFullDto;
import ru.practicum.ewm.dto.EventShortDto;
import ru.practicum.ewm.exception.BadRequestException;
import ru.practicum.ewm.mapper.EventMapper;
import ru.practicum.ewm.model.EventSorts;
import ru.practicum.ewm.service.EventService;
import ru.practicum.ewm.service.RequestService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.ewm.util.Constants.DATE_FORMAT;

@Slf4j
@RequiredArgsConstructor
@RestController
@Validated
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;
    private final RequestService requestService;
    private final EventMapper eventMapper;
    private final HttpServletRequest request;

    @GetMapping
    public ResponseEntity<List<EventShortDto>> getAll(
            @RequestParam(name = "text", defaultValue = "") String text,
            @RequestParam(name = "categories", required = false) List<Long> categories,
            @RequestParam(name = "paid", required = false) Boolean paid,
            @RequestParam(value = "rangeStart", required = false)
            @DateTimeFormat(pattern = DATE_FORMAT) @Valid LocalDateTime rangeStart,
            @RequestParam(value = "rangeEnd", required = false)
            @DateTimeFormat(pattern = DATE_FORMAT) @Valid LocalDateTime rangeEnd,
            @RequestParam(name = "onlyAvailable", defaultValue = "false") Boolean onlyAvailable,
            @RequestParam(name = "sort", defaultValue = "EVENT_DATE") EventSorts sort,
            @RequestParam(value = "from", defaultValue = "0")
            @Min(value = 0, message = "minimum value for from param is 0") Integer from,
            @RequestParam(value = "size", defaultValue = "10")
            @Min(value = 1, message = "minimum value for size param is 1") Integer size
        ) {

        log.info("Get all events, from {}, size {}, text {}, categories {}, rangeStart {}, rangeEnd {}, " +
                        "sort {}, onlyAvailable {}, paid {}",
                from, size, text, categories, rangeStart, rangeEnd, sort, onlyAvailable, paid);

        if (rangeStart != null && rangeEnd != null && rangeEnd.isBefore(rangeStart)) {
            throw new BadRequestException("range end is before range start");
        }

        var events = eventService.getAllPublic(from, size, text, categories, paid, rangeStart, rangeEnd,
                onlyAvailable, sort, request);
        return ResponseEntity.ok(
                events.stream().map(eventMapper::entityToEventShortDto).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventFullDto> get(@PathVariable("id") Long id) {
        log.info("Get event by id: {}", id);
        var event = eventService.findById(id, request);
        var fullDto = eventMapper.entityToEventFullDto(event);
        fullDto.setConfirmedRequests(requestService.requestsByEvent(event));
        return ResponseEntity.ok(fullDto);
    }

}