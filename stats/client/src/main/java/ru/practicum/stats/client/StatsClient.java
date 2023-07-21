package ru.practicum.stats.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.stats.dto.StatsResponse;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.stats.util.Constants.DATE_FORMAT;

@FeignClient(
        value = "stats",
        url = "${feign.url.stats}"
)
public interface StatsClient {
    @GetMapping
    ResponseEntity<List<StatsResponse>> getStats(
            @RequestParam(value = "start") @DateTimeFormat(pattern = DATE_FORMAT) @Valid LocalDateTime start,
            @RequestParam(value = "end") @DateTimeFormat(pattern = DATE_FORMAT) @Valid LocalDateTime end,
            @RequestParam(value = "uris", required = false) List<String> uris,
            @RequestParam(value = "unique", required = false) Boolean unique);
}
