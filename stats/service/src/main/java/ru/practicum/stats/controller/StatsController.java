package ru.practicum.stats.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.stats.dto.StatsResponse;
import ru.practicum.stats.exception.BadRequestException;
import ru.practicum.stats.service.HitService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.stats.util.Constants.DATE_FORMAT;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/stats")
public class StatsController {

    private final HitService hitService;

    @GetMapping
    public ResponseEntity<List<StatsResponse>> getStats(
            @RequestParam(value = "start") @DateTimeFormat(pattern = DATE_FORMAT) @Valid LocalDateTime start,
            @RequestParam(value = "end") @DateTimeFormat(pattern = DATE_FORMAT) @Valid LocalDateTime end,
            @RequestParam(value = "uris", required = false) List<String> uris,
            @RequestParam(value = "unique", required = false) Boolean unique) {
        log.info("Get stat hits by from {} to {}, uris {}, unique {}", start, end, uris, unique);

        if (start.isAfter(end)) {
            throw new BadRequestException("start parameter should be before end parameter");
        }

        var list = hitService.getStats(start, end, uris, unique);
        return ResponseEntity.ok(list);
    }

}
