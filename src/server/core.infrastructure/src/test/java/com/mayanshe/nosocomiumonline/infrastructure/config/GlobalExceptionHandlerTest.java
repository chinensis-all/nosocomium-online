package com.mayanshe.nosocomiumonline.infrastructure.config;

import com.mayanshe.nosocomiumonline.shared.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleNosocomiumException_shouldReturnCorrectStatusAndMessage() {
        NotFoundException ex = new NotFoundException();
        ResponseEntity<Map<String, Object>> response = handler.handleNosocomiumException(ex);

        assertEquals(404, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().get("code"));
        assertEquals("资源不存在", response.getBody().get("message"));
    }

    @Test
    void handleOtherException_shouldReturn500AndGenericMessage() {
        RuntimeException ex = new NullPointerException("Oops");
        ResponseEntity<Map<String, Object>> response = handler.handleOtherException(ex);

        assertEquals(500, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(500, response.getBody().get("code"));
        assertEquals("请求失败，请重试", response.getBody().get("message"));
    }
}
