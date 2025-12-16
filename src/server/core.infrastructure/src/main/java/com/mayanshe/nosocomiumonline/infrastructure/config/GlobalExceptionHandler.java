package com.mayanshe.nosocomiumonline.infrastructure.config;

import com.mayanshe.nosocomiumonline.shared.exception.INosocomiumException;
import com.mayanshe.nosocomiumonline.shared.exception.AbstractHttpException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
@Profile({ "prod", "release", "test", "dev" })
public class GlobalExceptionHandler {

    @ExceptionHandler(AbstractHttpException.class)
    public ResponseEntity<Map<String, Object>> handleNosocomiumException(AbstractHttpException e) {
        Map<String, Object> body = new HashMap<>();
        body.put("code", e.getHttpStatus());
        body.put("message", e.getMessage());
        return ResponseEntity.status(e.getHttpStatus()).body(body);
    }

    // Capture other INosocomiumException implementations if any, separate from
    // AbstractHttpException
    // For now AbstractHttpException covers all requirements, but strictly catching
    // interface is also good if possible.
    // However, interface cannot be exception handler target directly without
    // extending Throwable, which AbstractHttpException does.

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleOtherException(Exception e) {
        log.error("Unhandled exception caught: ", e);
        Map<String, Object> body = new HashMap<>();
        body.put("code", 500);
        body.put("message", "请求失败，请重试");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
