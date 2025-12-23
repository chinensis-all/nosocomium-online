package com.mayanshe.nosocomiumonline.infrastructure.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * NotBlankSizeValidator: 非空验证Size实现类
 *
 * @author zhangxihai
 */
public class NotBlankSizeValidator implements ConstraintValidator<NotBlankSize, String> {
    private int min;
    private int max;
    private String message;

    @Override
    public void initialize(NotBlankSize constraintAnnotation) {
        this.min = constraintAnnotation.min();
        this.max = constraintAnnotation.max();
        this.message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(String input, ConstraintValidatorContext context) {
        if (input == null || input.trim().isEmpty()) {
            return true;
        }

        if (input.length() < min || input.length() > max) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(message)
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
