package ru.yandex.practicum.filmorate.validation;

import ru.yandex.practicum.filmorate.validation.interfaces.ValidDate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class DateValidator implements ConstraintValidator<ValidDate, LocalDate> {
    private final LocalDate minDateValue = LocalDate.of(1895, 12, 28);

    @Override
    public boolean isValid(LocalDate date, ConstraintValidatorContext context) {
        return date != null && !date.isBefore(minDateValue);
    }
}
