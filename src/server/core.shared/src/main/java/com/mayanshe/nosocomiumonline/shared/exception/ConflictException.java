package com.mayanshe.nosocomiumonline.shared.exception;

/**
 * 409 Conflict Exception.
 */
public class ConflictException extends AbstractHttpException {
    private static final int HTTP_STATUS = 409;
    private static final String DEFAULT_MESSAGE = "资源冲突";

    public ConflictException() {
        super(HTTP_STATUS, DEFAULT_MESSAGE, null);
    }

    public ConflictException(String message) {
        super(HTTP_STATUS, DEFAULT_MESSAGE, message);
    }
}
