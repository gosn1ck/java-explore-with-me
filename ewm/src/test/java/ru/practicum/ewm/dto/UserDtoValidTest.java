package ru.practicum.ewm.dto;

import lombok.val;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {ValidationAutoConfiguration.class})
class UserDtoValidTest {

    private static final String NAME = "Ivan";
    private static final String EMAIL = "ivan@yandex.ru";

    @Autowired
    private Validator underTest;

    @DisplayName("Запрос создания пользователя проходит проверку валидации")
    @Test
    void shouldCheckValidUserDto() {
        val dto = new UserDto();
        dto.setName(NAME);
        dto.setEmail(EMAIL);

        Set<ConstraintViolation<UserDto>> validates = underTest.validate(dto);
        assertEquals(0, validates.size());
    }

    @DisplayName("Запрос создания пользователя не должен проходить валидацию с пустым названием")
    @Test
    void shouldNotCheckUserEmptyName() {
        val dtoEmptyName = new UserDto();
        dtoEmptyName.setName(" ");
        dtoEmptyName.setEmail(EMAIL);

        Set<ConstraintViolation<UserDto>> validates = underTest.validate(dtoEmptyName);
        assertTrue(validates.size() > 0);
        assertEquals(validates.stream().findFirst().get().getMessage(), "Name should not be empty");

        val dtoNullName = new UserDto();
        dtoNullName.setEmail(EMAIL);

        validates = underTest.validate(dtoNullName);
        assertTrue(validates.size() > 0);
        assertEquals(validates.stream().findFirst().get().getMessage(), "Name should not be empty");

    }

}
