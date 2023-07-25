package ru.practicum.stats.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.practicum.stats.dto.HitDto;
import ru.practicum.stats.dto.HitResponse;

import javax.validation.Valid;

@FeignClient(
        value = "hit",
        url = "${feign.url.hit}"
)
public interface HitClient {
    @PostMapping
    ResponseEntity<HitResponse> add(@Valid @RequestBody HitDto dto);
}