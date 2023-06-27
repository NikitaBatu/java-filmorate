package ru.yandex.practicum.filmorate.validation.interfaces;

import ru.yandex.practicum.filmorate.validation.ValidatorNoSpace;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = ValidatorNoSpace.class)
@Documented
public @interface NoSpace {
    String message() default "Логин не должен содержать пробелы";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
