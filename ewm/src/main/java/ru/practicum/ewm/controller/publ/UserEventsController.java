package ru.practicum.ewm.controller.publ;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.practicum.ewm.dto.*;
import ru.practicum.ewm.mapper.EventMapper;
import ru.practicum.ewm.service.EventService;
import ru.practicum.ewm.service.RequestService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("users/{userId}/events")
public class UserEventsController {

    private final EventService eventService;
    private final RequestService requestService;
    private final EventMapper eventMapper;

    @GetMapping
    public ResponseEntity<List<EventShortDto>> getAll(
            @PathVariable("userId") Long userId,
            @RequestParam(defaultValue = "0")
            @Min(value = 0, message = "minimum value for from param is 0") Integer from,
            @RequestParam(defaultValue = "10")
            @Min(value = 1, message = "minimum value for size param is 1") Integer size) {
        log.info("Get events of userId {} events, from {}, size {}", userId, from, size);
        var events = eventService.getAllByInitiator(userId, from, size);
        return ResponseEntity.ok(
                events.stream().map(eventMapper::entityToEventShortDto).collect(Collectors.toList()));
    }

    @GetMapping(path = "/{eventId}")
    public ResponseEntity<EventFullDto> getEvent(
            @PathVariable("userId") Long userId,
            @PathVariable("eventId") Long eventId) {
        log.info("Get userId {} event {}", userId, eventId);
        var event = eventService.getUserEvent(userId, eventId);
        return ResponseEntity.ok(eventMapper.entityToEventFullDto(event));
    }

    @PatchMapping(consumes = "application/json", path = "/{eventId}")
    public ResponseEntity<EventFullDto> update(
            @RequestBody @Valid UpdateEventUserRequest dto,
            @PathVariable("userId") Long userId,
            @PathVariable("eventId") Long eventId) {
        log.info("Update eventId {} by userId {}, by dto {}", eventId, userId, dto);
        var event = eventService.update(eventId, userId, dto);
        var fullDto = eventMapper.entityToEventFullDto(event);
        fullDto.setConfirmedRequests(requestService.requestsByEvent(event));
        return ResponseEntity.ok(fullDto);
    }

    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<EventFullDto> add(@Valid @RequestBody NewEventDto dto,
                                            @PathVariable("userId") Long userId) {
        log.info("New event registration {}", dto);
        var savedEvent = eventService.add(dto, userId);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedEvent.getId()).toUri();
        var eventFullDto = eventMapper.entityToEventFullDto(savedEvent);
        eventFullDto.setViews(0);
        eventFullDto.setConfirmedRequests(0);
        return ResponseEntity.created(location).body(eventFullDto);
    }

}
