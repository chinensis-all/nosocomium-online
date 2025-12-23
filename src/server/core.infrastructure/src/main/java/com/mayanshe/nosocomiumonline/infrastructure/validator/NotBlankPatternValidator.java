package com.mayanshe.nosocomiumonline.infrastructure.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * NotBlankPatternValidator: 非空验证Pattern实现类
 *
 * @author zhangxihai
 */
public class NotBlankPatternValidator implements ConstraintValidator<NotBlankPattern, String> {
    private String regexp;
    private String message;

    @Override
    public void initialize(NotBlankPattern constraintAnnotation) {
        this.regexp = constraintAnnotation.regexp();
        this.message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(String input, ConstraintValidatorContext context) {
        if (input == null || input.trim().isEmpty()) {
            return true;
        }

        if (!input.matches(regexp)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(message)
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
