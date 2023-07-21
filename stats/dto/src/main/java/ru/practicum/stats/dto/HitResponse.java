package ru.practicum.stats.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HitResponse {
    private Integer id;
    private String app;
    private String uri;
    private String ip;
    private String timestamp;
}
