package ru.practicum.stats.mapper;

import lombok.val;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.stats.dto.HitDto;
import ru.practicum.stats.model.Hit;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;
import static ru.practicum.stats.util.Constants.DATE_FORMAT;

@SpringBootTest(classes = {HitMapperImpl.class})
class HitMapperTest {

    @Autowired
    private HitMapper underTest;

    private static final String APP = "ewm-main-service";
    private static final String URI = "/events/1";
    private static final String IP = "192.168.1.1";
    private static final String TIME = "2022-09-06 11:00:23";

    @DisplayName("Запрос хита мэпится в хит для записи в БД")
    @Test
    void shouldMapHitDtoToHit() {
        val dto = getDto();
        val entity = underTest.dtoToEntity(dto);
        val formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);

        assertEquals(dto.getApp(), entity.getApp());
        assertEquals(dto.getUri(), entity.getUri());
        assertEquals(dto.getIp(), entity.getIp());
        val timestamp = LocalDateTime.parse(dto.getTimestamp(), formatter);
        assertEquals(timestamp, entity.getTimestamp());
    }

    @DisplayName("Комментарий мэпится в комментарий для ответа контроллера")
    @Test
    void shouldMapHitToHitResponse() {
        val entity = getEntity();
        val response = underTest.entityToResponse(entity);

        assertEquals(response.getId(), entity.getId());
        assertEquals(response.getApp(), entity.getApp());
        assertEquals(response.getUri(), entity.getUri());
        assertEquals(response.getIp(), entity.getIp());

        assertEquals(response.getTimestamp(), entity.getTimestamp().format(DateTimeFormatter.ofPattern(DATE_FORMAT)));
    }

    private static HitDto getDto() {
        return HitDto.builder()
                .app(APP)
                .uri(URI)
                .ip(IP)
                .timestamp(TIME)
                .build();
    }

    private static Hit getEntity() {
        return Hit.builder()
                .id(1L)
                .app(APP)
                .uri(URI)
                .ip(IP)
                .timestamp(LocalDateTime.of(2023, 7, 23, 10, 0))
                .build();
    }

}