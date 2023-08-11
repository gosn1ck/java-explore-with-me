package ru.practicum.ewm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    @NotBlank(message = "text field should not be empty")
    @Size(min = 1, max = 5000, message = "name field should be min 1, max 5000")
    private String text;
}