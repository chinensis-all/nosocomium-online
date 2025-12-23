package com.mayanshe.nosocomiumonline.infrastructure.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * AnyInListValidator: 任意值在列表中验证实现类
 *
 * @author zhangxihai
 */
public class AnyInListValidator implements ConstraintValidator<AnyInList, Object> {
    private Set<String> allowedValues;

    private boolean matchEnumName;

    @Override
    public void initialize(AnyInList annotation) {
        allowedValues = Arrays.stream(annotation.value()).filter(Objects::nonNull).collect(Collectors.toSet());
        matchEnumName = annotation.matchEnumName();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        // ✅ 统一转换成字符串再比对
        String strValue;

        if (value instanceof Enum<?> e) {
            strValue = matchEnumName ? e.name() : e.toString();
        } else {
            strValue = String.valueOf(value);
        }

        return allowedValues.contains(strValue);
    }
}
