package ru.practicum.stats.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.stats.dto.StatsResponse;
import ru.practicum.stats.service.HitService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.practicum.stats.util.Constants.DATE_FORMAT;

@WebMvcTest({StatsController.class})
class StatsControllerTest {

    @Autowired
    private MockMvc mvc;
    @MockBean
    private HitService hitService;
    @Autowired
    private ObjectMapper objectMapper;

    private static final String END_POINT_PATH = "/stats";

    @Test
    @DisplayName("Ручка создания по валидному запросу возвращает 200 и json c статистикой хитов")
    void shouldGetStats() throws Exception {

        var start = LocalDateTime.of(2022, 9, 6, 11, 0);
        var startBySting = DateTimeFormatter.ofPattern(DATE_FORMAT).format(start);

        var end = LocalDateTime.of(2022, 9, 6, 12, 0);
        var endBySting = DateTimeFormatter.ofPattern(DATE_FORMAT).format(end);

        var stats = new StatsResponse("ewm-main-service", "/events/1", 1);

        when(hitService.getStats(start, end, null, null))
                .thenReturn(List.of(stats));

        mvc.perform(get(END_POINT_PATH)
                        .param("start", startBySting)
                        .param("end", endBySting))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(stats))));

    }

    @Test
    @DisplayName("Ручка получения статистики возвращает 400 с некорректными параметрами")
    void shouldNotGetStatsInvalidParams() throws Exception {
        // start parameter should be before end parameter
        var start = LocalDateTime.of(2022, 9, 6, 11, 0);
        var startBySting = DateTimeFormatter.ofPattern(DATE_FORMAT).format(start);

        var end = LocalDateTime.of(2022, 9, 6, 10, 0);
        var endBySting = DateTimeFormatter.ofPattern(DATE_FORMAT).format(end);

        mvc.perform(get(END_POINT_PATH)
                        .param("start", startBySting)
                        .param("end", endBySting))
                .andExpect(status().isBadRequest());

        // Failed to convert value of type 'java.lang.String'
        endBySting = "2022-09-06";

        mvc.perform(get(END_POINT_PATH)
                        .param("start", startBySting)
                        .param("end", endBySting))
                .andExpect(status().isBadRequest());

        // MissingServletRequestParameterException
        mvc.perform(get(END_POINT_PATH)
                        .param("start", startBySting))
                .andExpect(status().isBadRequest());

    }

}
