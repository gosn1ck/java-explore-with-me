package ru.practicum.ewm.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(FIELD)
@Retention(RUNTIME)
@Constraint(validatedBy = DateTimeFormatValidator.class)
public @interface ValidDateTimeFormat {
    String message() default "Date Format is not valid. Format should be \"yyyy-MM-dd HH:mm:ss\"";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}