package com.mayanshe.nosocomiumonline.shared.exception;

/**
 * 401 Unauthorized Exception.
 */
public class UnauthorizedException extends AbstractHttpException {
    private static final int HTTP_STATUS = 401;
    private static final String DEFAULT_MESSAGE = "未授权访问";

    public UnauthorizedException() {
        super(HTTP_STATUS, DEFAULT_MESSAGE, null);
    }

    public UnauthorizedException(String message) {
        super(HTTP_STATUS, DEFAULT_MESSAGE, message);
    }
}
