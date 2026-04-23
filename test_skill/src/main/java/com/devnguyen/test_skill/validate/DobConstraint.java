package com.devnguyen.test_skill.validate;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = { DobValidate.class })
public @interface DobConstraint {
    // 1
    String message() default "Ngày sinh không hợp lệ";

    int min();

    // 2
    Class<?>[] groups() default {};

    // 3
    Class<? extends Payload>[] payload() default {};
}
