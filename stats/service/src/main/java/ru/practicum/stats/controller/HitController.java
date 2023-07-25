package ru.practicum.stats.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.practicum.stats.dto.HitDto;
import ru.practicum.stats.dto.HitResponse;
import ru.practicum.stats.mapper.HitMapper;
import ru.practicum.stats.service.HitService;

import javax.validation.Valid;
import java.net.URI;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/hit")
public class HitController {

    private final HitService hitService;
    private final HitMapper hitMapper;

    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<HitResponse> add(@Valid @RequestBody HitDto dto) {
        log.info("New hit registration {}", dto);
        var savedHit = hitService.add(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedHit.getId()).toUri();
        return ResponseEntity.created(location)
                .body(hitMapper.entityToResponse(savedHit));
    }

}
