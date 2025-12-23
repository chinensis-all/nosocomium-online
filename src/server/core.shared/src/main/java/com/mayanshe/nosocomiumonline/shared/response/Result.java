package com.mayanshe.nosocomiumonline.shared.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 格式化的响应结果
 * @param <T>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {
    private Integer code;
    private String message;
    private T data;

    public static <T> Result<T> success(T data) {
        return Result.<T>builder()
                .code(200)
                .message("success")
                .data(data)
                .build();
    }

    public static <T> Result<T> success() {
        return success(null);
    }

    public static <T> Result<T> error(Integer code, String message) {
        return Result.<T>builder()
                .code(code)
                .message(message)
                .build();
    }
}
