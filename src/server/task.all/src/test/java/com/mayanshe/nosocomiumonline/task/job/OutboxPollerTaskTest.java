package com.mayanshe.nosocomiumonline.task.job;

import com.mayanshe.nosocomiumonline.infrastructure.persistence.entity.OutboxEntity;
import com.mayanshe.nosocomiumonline.infrastructure.persistence.mapper.OutboxMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

@SpringBootTest(classes = OutboxPollerTask.class)
public class OutboxPollerTaskTest {

    @Autowired
    private OutboxPollerTask outboxPollerTask;

    @MockBean
    private OutboxMapper outboxMapper;

    @Test
    public void processOutbox_shouldProcessAndMarkSent() {
        // Given
        OutboxEntity event = OutboxEntity.builder()
                .id(1L)
                .status("NEW")
                .retryCount(0)
                .eventType("TestEvent")
                .build();

        when(outboxMapper.selectNewEvents(anyInt())).thenReturn(Collections.singletonList(event));

        // When
        outboxPollerTask.processOutbox();

        // Then
        verify(outboxMapper, times(1)).update(argThat(e -> "SENT".equals(e.getStatus())));
    }
}
