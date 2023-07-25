package ru.practicum.stats.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.stats.dto.HitDto;
import ru.practicum.stats.mapper.HitMapper;
import ru.practicum.stats.mapper.HitMapperImpl;
import ru.practicum.stats.model.Hit;
import ru.practicum.stats.service.HitService;

import java.time.LocalDateTime;

import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({HitController.class, HitMapperImpl.class})
class HitControllerTest {

    @Autowired
    private MockMvc mvc;
    @MockBean
    private HitService hitService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private HitMapper hitMapper;

    private static final String APP = "ewm-main-service";
    private static final String URI = "/events/1";
    private static final String IP = "192.168.1.1";
    private static final String TIME = "2022-09-06 11:00:01";
    private static final String END_POINT_PATH = "/hit";

    @Test
    @DisplayName("Ручка создания по валидному запросу возвращает 201 и json c id нового хита")
    void shouldCreateHit() throws Exception {
        var dto = getDto();
        var hit = getHit();

        given(hitService.add(dto)).willReturn(hit);

        mvc.perform(post(END_POINT_PATH)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(hit.getId()))
                .andExpect(jsonPath("$.app").value(hit.getApp()))
                .andExpect(jsonPath("$.ip").value(hit.getIp()))
                .andExpect(jsonPath("$.uri").value(hit.getUri()));
    }

    @Test
    @DisplayName("Ручка создания в случае не валидного dto возвращает 400")
    void shouldNotCreateHit() throws Exception {
        var dto = getDto();
        dto.setUri(null);

        mvc.perform(post(END_POINT_PATH)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());

    }

    private static HitDto getDto() {
        return HitDto.builder()
                .app(APP)
                .uri(URI)
                .ip(IP)
                .timestamp(TIME)
                .build();
    }

    private static Hit getHit() {
        return Hit.builder()
                .id(1L)
                .app(APP)
                .uri(URI)
                .ip(IP)
                .timestamp(LocalDateTime.of(2023, 7, 23, 10, 0))
                .build();
    }

}