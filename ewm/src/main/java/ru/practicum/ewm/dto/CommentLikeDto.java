package ru.practicum.ewm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.model.LikeType;

import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentLikeDto {
    @NotNull(message = "comment field should not be null")
    private Long comment;
    @NotNull(message = "type field should not be null")
    private LikeType type;
}