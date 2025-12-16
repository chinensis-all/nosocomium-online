package com.mayanshe.nosocomiumonline.shared.exception;

/**
 * 500 Internal Server Error Exception.
 */
public class InternalServerErrorException extends AbstractHttpException {
    private static final int HTTP_STATUS = 500;
    private static final String DEFAULT_MESSAGE = "系统内部错误";

    public InternalServerErrorException() {
        super(HTTP_STATUS, DEFAULT_MESSAGE, null);
    }

    public InternalServerErrorException(String message) {
        super(HTTP_STATUS, DEFAULT_MESSAGE, message);
    }
}
