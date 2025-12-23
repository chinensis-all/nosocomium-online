package com.mayanshe.nosocomiumonline.infrastructure.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 注解 : 非空验证Pattern
 *
 * @author zhangxihai
 */
@Constraint(validatedBy = NotBlankPatternValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NotBlankPattern {
    String regexp();

    String message() default "输入值不符合指定的正则表达式";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
