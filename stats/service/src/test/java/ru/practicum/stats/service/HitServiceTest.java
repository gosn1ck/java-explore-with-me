package ru.practicum.stats.service;

import lombok.val;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.stats.dto.HitDto;
import ru.practicum.stats.repository.HitRepository;

import java.time.LocalDateTime;
import java.util.List;

import static java.lang.Boolean.TRUE;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class HitServiceTest {

    @Autowired
    private HitService underTest;
    @Autowired
    private HitRepository repository;

    private static final String APP = "ewm-main-service";
    private static final String URI = "/events/1";
    private static final String IP = "192.168.1.1";
    private static final String TIME = "2022-09-06 11:00:01";

    @AfterEach
    public void cleanUpEach() {
        repository.deleteAll();
    }

    @DisplayName("Хит добавлна в сервис")
    @Test
    void shouldAddItem() {
        val dto = getDto();
        val hit = underTest.add(dto);

        assertNotNull(hit.getId());
        assertEquals(dto.getApp(), hit.getApp());
        assertEquals(dto.getUri(), hit.getUri());
        assertEquals(dto.getIp(), hit.getIp());

    }

    @DisplayName("Возвращает статистику по хитам")
    @Test
    void shouldGetStats() {
        var start = LocalDateTime.of(2022, 9, 6, 11, 0);
        var end = LocalDateTime.of(2022, 9, 6, 12, 0);

        var dto = getDto();
        underTest.add(dto);
        dto.setTimestamp("2022-09-06 11:00:02");
        underTest.add(dto);


        var stats = underTest.getStats(start, end, null, null);
        assertEquals(stats.size(), 1);
        assertEquals(stats.get(0).getHits(), 2);

        stats = underTest.getStats(start, end, null, TRUE);
        assertEquals(stats.size(), 1);
        assertEquals(stats.get(0).getHits(), 1);

        dto.setTimestamp("2022-09-06 11:00:03");
        dto.setIp("192.168.1.2");
        dto.setUri("/events/2");
        underTest.add(dto);

        stats = underTest.getStats(start, end, List.of("/events/1"), TRUE);
        assertEquals(stats.size(), 1);
        assertEquals(stats.get(0).getHits(), 1);

        stats = underTest.getStats(start, end, List.of("/events/1"), null);
        assertEquals(stats.size(), 1);

        stats = underTest.getStats(start, end, null, null);
        assertEquals(stats.size(), 2);

    }

    private static HitDto getDto() {
        return HitDto.builder()
                .app(APP)
                .uri(URI)
                .ip(IP)
                .timestamp(TIME)
                .build();
    }

}
