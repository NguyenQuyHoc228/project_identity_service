package com.devnguyen.test_skill.validate;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class DobValidate implements ConstraintValidator<DobConstraint, LocalDate> {

    private int min;
    // ham xu ly data
    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {

        // mỗi annotation thì nên xử lý 1 constraint nhất định.
        if (Objects.isNull(value)){
            return true;
        }

        long years = ChronoUnit.YEARS.between(value, LocalDate.now());

        return years >= min;
    }



    //
    @Override
    public void initialize(DobConstraint constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        min = constraintAnnotation.min();
    }
}
