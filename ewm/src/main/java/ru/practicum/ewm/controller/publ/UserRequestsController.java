package ru.practicum.ewm.controller.publ;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.practicum.ewm.dto.RequestResponse;
import ru.practicum.ewm.mapper.RequestMapper;
import ru.practicum.ewm.service.RequestService;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("users/{userId}/requests")
public class UserRequestsController {

    private final RequestService requestService;
    private final RequestMapper requestMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<RequestResponse> add(
            @PathVariable("userId") Long userId,
            @RequestParam("eventId") Long eventId) {
        log.info("New request registration userId {}, eventId {}", userId, eventId);
        var savedRequest = requestService.add(userId, eventId);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedRequest.getId()).toUri();
        return ResponseEntity.created(location).body(requestMapper.entityToResponse(savedRequest));
    }

    @GetMapping
    public ResponseEntity<List<RequestResponse>> getAll(@PathVariable("userId") Long userId) {
        log.info("Get requests of userId {}", userId);
        var requests = requestService.getAll(userId);
        return ResponseEntity.ok(
                requests.stream().map(requestMapper::entityToResponse).collect(Collectors.toList()));
    }

    @PatchMapping("/{requestId}/cancel")
    public ResponseEntity<RequestResponse> update(
            @PathVariable("userId") Long userId,
            @PathVariable("requestId") Long requestId) {
        log.info("Cancel by userId {}, requestId {} ", userId,requestId);
        return ResponseEntity.ok(requestMapper.entityToResponse(requestService.cancel(userId, requestId)));
    }

}