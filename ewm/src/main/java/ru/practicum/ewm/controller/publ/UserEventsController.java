package ru.practicum.ewm.controller.publ;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.practicum.ewm.dto.*;
import ru.practicum.ewm.exception.BadRequestException;
import ru.practicum.ewm.mapper.CommentMapper;
import ru.practicum.ewm.mapper.EventMapper;
import ru.practicum.ewm.mapper.RequestMapper;
import ru.practicum.ewm.service.CommentService;
import ru.practicum.ewm.service.EventService;
import ru.practicum.ewm.service.RequestService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.ewm.util.Constants.FORMATTER;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("users/{userId}/events")
public class UserEventsController {

    private final EventService eventService;
    private final RequestService requestService;
    private final CommentService commentService;
    private final EventMapper eventMapper;
    private final RequestMapper requestMapper;
    private final CommentMapper commentMapper;

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

        if (dto.getEventDate() != null) {
            var date = LocalDateTime.parse(dto.getEventDate(), FORMATTER);
            if (date.isBefore(LocalDateTime.now().plusHours(2))) {
                throw new BadRequestException("impossible to update event");
            }
        }

        var event = eventService.update(eventId, userId, dto);
        var fullDto = eventMapper.entityToEventFullDto(event);
        fullDto.setConfirmedRequests(requestService.requestsByEvent(event));
        fullDto.setComments(
                commentService.findAllByEventId(eventId)
                        .stream()
                        .map(commentMapper::entityToShortResponse)
                        .map(commentService::findLikes)
                        .collect(Collectors.toList()));
        return ResponseEntity.ok(fullDto);
    }

    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<EventFullDto> add(
            @Valid @RequestBody NewEventDto dto,
            @PathVariable("userId") Long userId) {
        log.info("New event registration {}", dto);
        var savedEvent = eventService.add(dto, userId);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedEvent.getId()).toUri();
        var eventFullDto = eventMapper.entityToEventFullDto(savedEvent);
        eventFullDto.setViews(0);
        eventFullDto.setConfirmedRequests(0);
        eventFullDto.setComments(new ArrayList<>());
        return ResponseEntity.created(location).body(eventFullDto);
    }

    @GetMapping("/{eventId}/requests")
    public ResponseEntity<List<ParticipationRequestDto>> getRequests(
            @PathVariable("userId") Long userId,
            @PathVariable("eventId") Long eventId) {
        log.info("Get participation request user id {}, event id {}", userId, eventId);
        var requests = eventService.getRequests(userId, eventId);
        return ResponseEntity.ok(
                requests.stream().map(requestMapper::entityToParticipationRequest).collect(Collectors.toList()));
    }

    @PatchMapping("{eventId}/requests")
    public ResponseEntity<EventRequestStatusUpdateResult> updateParticipationRequest(
            @PathVariable("userId") Long userId,
            @PathVariable("eventId") Long eventId,
            @RequestBody EventRequestStatusUpdateRequest dto) {
        log.info("Update status participation request user id {}, event id {}, dto {}", userId, eventId, dto);
        return ResponseEntity.ok(eventService.updateParticipationRequest(userId, eventId, dto));
    }

    @PostMapping("/{eventId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<CommentResponse> addComment(
            @PathVariable("userId") Long userId,
            @PathVariable("eventId") Long eventId,
            @Valid @RequestBody CommentDto dto) {
        log.info("New comment registration {}, from userId {} to eventId {}", dto, userId, eventId);
        var savedComment = commentService.add(dto, userId, eventId);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedComment.getId()).toUri();
        return ResponseEntity.created(location).body(commentMapper.entityToResponse(savedComment));
    }

    @PatchMapping("/{eventId}/comments/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(
            @PathVariable("userId") Long userId,
            @PathVariable("eventId") Long eventId,
            @PathVariable("commentId") Long commentId,
            @Valid @RequestBody CommentDto dto) {
        log.info("Update comment id {}, from userId {} to eventId {}, with dto {}", commentId, userId, eventId, dto);
        var comment = commentService.update(dto, userId, eventId, commentId);
        return ResponseEntity.ok(commentMapper.entityToResponse(comment));
    }

    @GetMapping("/{eventId}/comments/{commentId}")
    public ResponseEntity<CommentResponse> getComment(
            @PathVariable("userId") Long userId,
            @PathVariable("eventId") Long eventId,
            @PathVariable("commentId") Long commentId) {
        log.info("Get userId {} eventId {} commentId {}", userId, eventId, commentId);
        var comment = commentService.findCommentById(userId, eventId, commentId);
        return ResponseEntity.ok(commentMapper.entityToResponse(comment));
    }

}