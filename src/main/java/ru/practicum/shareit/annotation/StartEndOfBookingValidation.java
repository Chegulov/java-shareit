package ru.practicum.shareit.annotation;


import ru.practicum.shareit.validateGroups.StartEndOfBookingValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = StartEndOfBookingValidator.class)
@Target({TYPE, ANNOTATION_TYPE})
@Retention(RUNTIME)
public @interface StartEndOfBookingValidation {
    String message() default "Ошибка в введении данных начала и конца бронирования";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
