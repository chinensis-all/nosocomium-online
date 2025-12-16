package com.mayanshe.nosocomiumonline.shared.exception;

/**
 * 400 Bad Request Exception.
 */
public class BadRequestException extends AbstractHttpException {
    private static final int HTTP_STATUS = 400;
    private static final String DEFAULT_MESSAGE = "请求参数错误";

    public BadRequestException() {
        super(HTTP_STATUS, DEFAULT_MESSAGE, null);
    }

    public BadRequestException(String message) {
        super(HTTP_STATUS, DEFAULT_MESSAGE, message);
    }
}
