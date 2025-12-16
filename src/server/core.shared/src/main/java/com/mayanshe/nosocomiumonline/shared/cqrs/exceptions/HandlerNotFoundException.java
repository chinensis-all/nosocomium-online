package com.mayanshe.nosocomiumonline.shared.cqrs.exceptions;

import com.mayanshe.nosocomiumonline.shared.exception.InternalServerErrorException;

/**
 * 当无法找到对应的 CommandHandler 或 QueryHandler 时抛出。
 * 继承自统一异常体系中的 InternalServerErrorException (500)。
 */
public class HandlerNotFoundException extends InternalServerErrorException {
    public HandlerNotFoundException(String message) {
        super(message);
    }
}
