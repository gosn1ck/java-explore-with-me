package ru.practicum.ewm.validator;

import ru.practicum.ewm.dto.NewEventDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

import static ru.practicum.ewm.util.Constants.FORMATTER;

public class EventDateValidator implements ConstraintValidator<EventDate, NewEventDto> {

    @Override
    public boolean isValid(NewEventDto value, ConstraintValidatorContext context) {
        if (value.getEventDate() == null) {
            return true;
        }
        var eventDate = LocalDateTime.parse(value.getEventDate(), FORMATTER);
        return eventDate.minusHours(2).isAfter(LocalDateTime.now());
    }
}