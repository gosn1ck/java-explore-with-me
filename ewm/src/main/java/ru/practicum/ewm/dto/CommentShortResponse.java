package ru.practicum.ewm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentShortResponse {
    private Long id;
    private String text;
    private String created;
    private Long likes;
    private Long dislikes;
}