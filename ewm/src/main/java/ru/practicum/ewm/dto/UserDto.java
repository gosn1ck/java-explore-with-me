package ru.practicum.ewm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    @NotBlank(message = "Name should not be empty")
    private String name;
    @NotEmpty(message = "Email should not be empty")
    @Email(message = "Email is not valid")
    private String email;
}
