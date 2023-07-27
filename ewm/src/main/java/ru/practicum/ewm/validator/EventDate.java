package ru.practicum.ewm.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = EventDateValidator.class)
@Documented
public @interface EventDate {
    String message() default "event date must be before 2 hours of now";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
