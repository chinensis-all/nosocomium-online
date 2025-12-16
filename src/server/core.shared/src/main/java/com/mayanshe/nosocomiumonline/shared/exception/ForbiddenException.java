package com.mayanshe.nosocomiumonline.shared.exception;

/**
 * 403 Forbidden Exception.
 */
public class ForbiddenException extends AbstractHttpException {
    private static final int HTTP_STATUS = 403;
    private static final String DEFAULT_MESSAGE = "无权限访问";

    public ForbiddenException() {
        super(HTTP_STATUS, DEFAULT_MESSAGE, null);
    }

    public ForbiddenException(String message) {
        super(HTTP_STATUS, DEFAULT_MESSAGE, message);
    }
}
