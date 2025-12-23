package com.mayanshe.nosocomiumonline.infrastructure.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * 注解 : 字段值必须在指定列表中（支持基本类型及其包装类、字符串、枚举）
 *
 * @author zhangxihai
 */
@Documented
@Constraint(validatedBy = AnyInListValidator.class)  // 指定验证器类
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(AnyInList.List.class)
public @interface AnyInList {
    /**
     * 允许的值（统一用字符串表达）
     */
    String[] value();

    /**
     * 当被验证字段为枚举时，是否按枚举名匹配
     */
    boolean matchEnumName() default true;

    /**
     * 错误消息
     */
    String message() default "取值错误";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    @Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE, ElementType.TYPE_USE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface List {
        AnyInList[] value();
    }
}
