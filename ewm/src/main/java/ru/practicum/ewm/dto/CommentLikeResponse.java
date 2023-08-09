package ru.practicum.ewm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.model.LikeType;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentLikeResponse {
    private Long id;
    private String created;
    private LikeType type;
    private Long comment;
    private Long user;
}