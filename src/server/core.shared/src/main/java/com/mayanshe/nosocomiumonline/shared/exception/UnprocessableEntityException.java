package com.mayanshe.nosocomiumonline.shared.exception;

/**
 * 422 Unprocessable Entity Exception.
 */
public class UnprocessableEntityException extends AbstractHttpException {
    private static final int HTTP_STATUS = 422;
    private static final String DEFAULT_MESSAGE = "请求无法处理";

    public UnprocessableEntityException() {
        super(HTTP_STATUS, DEFAULT_MESSAGE, null);
    }

    public UnprocessableEntityException(String message) {
        super(HTTP_STATUS, DEFAULT_MESSAGE, message);
    }
}
