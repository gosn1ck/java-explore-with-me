package ru.practicum.stats.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.stats.validator.ValidDateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HitDto {
    @NotBlank(message = "app field should not be empty")
    private String app;
    @NotBlank(message = "uri field should not be empty")
    private String uri;
    @NotBlank(message = "ip field is not valid")
    @Pattern(
            regexp = "^((25[0-5]|(2[0-4]|1\\d|[1-9]|)\\d)\\.?\\b){4}$",
            message = "ip field is not valid"
    )
    private String ip;
    @ValidDateTimeFormat
    private String timestamp;
}
