package ru.practicum.ewm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    @NotBlank(message = "Name should not be empty")
    @Size(min = 2, max = 250, message = "name field should be min 2, max 250")
    private String name;
    @NotEmpty(message = "Email should not be empty")
    @Email(message = "Email is not valid")
    @Size(min = 6, max = 254, message = "name field should be min 2, max 250")
    private String email;
}