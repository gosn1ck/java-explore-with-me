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
class CategoryDtoValidTest {

    @Autowired
    private Validator underTest;

    private static final String NAME = "Концерты";

    @DisplayName("dto категории проходит проверку валидации")
    @Test
    void shouldCheckValidBookingDto() {
        val dto = new CategoryDto();
        dto.setName(NAME);
        Set<ConstraintViolation<CategoryDto>> validates = underTest.validate(dto);
        assertEquals(0, validates.size());
    }

    @DisplayName("dto категории не проходит проверку валидации")
    @Test
    void shouldCheckInvalidBookingDto() {
        val dto = new CategoryDto();
        dto.setName(null);

        Set<ConstraintViolation<CategoryDto>> validates = underTest.validate(dto);
        assertTrue(validates.size() > 0);
        assertEquals(validates.stream().findFirst().get().getMessage(), "name field should not be empty");

        dto.setName(" ");
        validates = underTest.validate(dto);
        assertTrue(validates.size() > 0);
        assertEquals(validates.stream().findFirst().get().getMessage(), "name field should not be empty");

    }

}