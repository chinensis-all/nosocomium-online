package com.mayanshe.nosocomiumonline.shared.exception;

/**
 * 404 Not Found Exception.
 */
public class NotFoundException extends AbstractHttpException {
    private static final int HTTP_STATUS = 404;
    private static final String DEFAULT_MESSAGE = "资源不存在";

    public NotFoundException() {
        super(HTTP_STATUS, DEFAULT_MESSAGE, null);
    }

    public NotFoundException(String message) {
        super(HTTP_STATUS, DEFAULT_MESSAGE, message);
    }
}
