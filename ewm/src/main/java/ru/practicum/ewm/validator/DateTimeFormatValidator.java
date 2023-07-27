package ru.practicum.ewm.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class DateTimeFormatValidator implements ConstraintValidator<ValidDateTimeFormat, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setLenient(false);
        try {
            sdf.parse(value);
        } catch (ParseException e) {
            return false;
        }
        return true;
    }
}
