package com.mayanshe.nosocomiumonline.infrastructure.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Size;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 注解 : 非空验证Size
 *
 * @author zhangxihai
 */
@Constraint(validatedBy = {NotBlankSizeValidator.class})
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NotBlankSize {
    /**
     * 最小长度
     */
    int min() default 1;

    /**
     * 最大长度
     */
    int max() default Integer.MAX_VALUE;

    /**
     * 错误消息
     */
    String message() default "字段不能为空且长度必须在 {min} 到 {max} 之间";

    /**
     * 分组
     */
    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
