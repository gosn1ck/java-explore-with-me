package ru.practicum.stats.dto;

import lombok.val;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {ValidationAutoConfiguration.class})
class HitDtoValidTest {

    private static final String APP = "ewm-main-service";
    private static final String URI = "/events/1";
    private static final String IP = "192.168.1.1";
    private static final String TIME = "2022-09-06 11:00:23";

    @Autowired
    private Validator underTest;

    @DisplayName("Запрос создание проходит проверку валидации")
    @Test
    void shouldCheckValidDto() {
        val dto = getDto();

        Set<ConstraintViolation<HitDto>> validates = underTest.validate(dto);
        assertEquals(0, validates.size());
    }

    @DisplayName("Запрос создания не должен проходить валидацию")
    @Test
    void shouldHaveErrorsInvalidDto() {
        val dto = getDto();
        dto.setApp(null);

        Set<ConstraintViolation<HitDto>> validates = underTest.validate(dto);
        assertTrue(validates.size() > 0);
        assertEquals(validates.stream().findFirst().get().getMessage(), "app field should not be empty");

        dto.setApp(" ");
        validates = underTest.validate(dto);
        assertTrue(validates.size() > 0);
        assertEquals(validates.stream().findFirst().get().getMessage(), "app field should not be empty");

        dto.setApp(APP);
        dto.setUri(null);
        validates = underTest.validate(dto);
        assertTrue(validates.size() > 0);
        assertEquals(validates.stream().findFirst().get().getMessage(), "uri field should not be empty");

        dto.setUri(" ");
        validates = underTest.validate(dto);
        assertTrue(validates.size() > 0);
        assertEquals(validates.stream().findFirst().get().getMessage(), "uri field should not be empty");

        dto.setUri(URI);
        dto.setIp(null);
        validates = underTest.validate(dto);
        assertTrue(validates.size() > 0);
        assertEquals(validates.stream().findFirst().get().getMessage(), "ip field is not valid");

        dto.setIp(" ");
        validates = underTest.validate(dto);
        assertTrue(validates.size() > 0);
        assertEquals(validates.stream().findFirst().get().getMessage(), "ip field is not valid");

        dto.setIp("123.3333");
        validates = underTest.validate(dto);
        assertTrue(validates.size() > 0);
        assertEquals(validates.stream().findFirst().get().getMessage(), "ip field is not valid");

        dto.setIp(IP);
        dto.setTimestamp(null);

        validates = underTest.validate(dto);
        assertTrue(validates.size() > 0);
        assertEquals(validates.stream().findFirst().get().getMessage(), "Date Format is not valid.");

        dto.setTimestamp(" ");
        validates = underTest.validate(dto);
        assertTrue(validates.size() > 0);
        assertEquals(validates.stream().findFirst().get().getMessage(), "Date Format is not valid.");

        dto.setTimestamp("2022-09-06");
        validates = underTest.validate(dto);
        assertTrue(validates.size() > 0);
        assertEquals(validates.stream().findFirst().get().getMessage(), "Date Format is not valid.");

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