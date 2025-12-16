package com.mayanshe.nosocomiumonline.shared.exception;

import lombok.Getter;

/**
 * HTTP 异常抽象基类。
 */
@Getter
public abstract class AbstractHttpException extends RuntimeException implements INosocomiumException {

    private final int httpStatus;
    private final String defaultMessage;

    protected AbstractHttpException(int httpStatus, String defaultMessage, String message) {
        super(message != null ? message : defaultMessage);
        this.httpStatus = httpStatus;
        this.defaultMessage = defaultMessage;
    }
}
