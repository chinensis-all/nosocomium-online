package com.mayanshe.nosocomiumonline.application.service.handler;

import com.mayanshe.nosocomiumonline.application.dto.MediaDto;
import com.mayanshe.nosocomiumonline.application.dto.command.UploadMediaCommand;
import com.mayanshe.nosocomiumonline.domain.media.model.Media;
import com.mayanshe.nosocomiumonline.domain.media.repository.MediaRepository;
import com.mayanshe.nosocomiumonline.domain.media.value.MediaType;
import com.mayanshe.nosocomiumonline.shared.storage.BucketType;
import com.mayanshe.nosocomiumonline.shared.storage.ObjectStorageService;
import com.mayanshe.nosocomiumonline.shared.valueobject.AggregateId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.mayanshe.nosocomiumonline.domain.kernel.eventing.OutboxRepository;
import com.mayanshe.nosocomiumonline.domain.media.event.MediaUploadedEvent;
import com.mayanshe.nosocomiumonline.shared.event.DomainEvent;
import java.util.List;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayInputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UploadMediaCommandHandlerTest {

    @Mock
    private MediaRepository mediaRepository;

    @Mock
    private ObjectStorageService objectStorageService;

    @Mock
    private OutboxRepository outboxRepository;

    @InjectMocks
    private UploadMediaCommandHandler handler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void handle_shouldUploadNewFile_whenMd5notExists() {
        // Arrange
        UploadMediaCommand command = new UploadMediaCommand(
                new ByteArrayInputStream(new byte[0]),
                "test.jpg",
                "image/jpeg",
                1024L,
                "new-md5",
                "private",
                "image",
                null,
                null);

        when(mediaRepository.loadByMd5("new-md5")).thenReturn(null);
        when(objectStorageService.getUrl(any(), any())).thenReturn("http://signed-url");
        doAnswer(invocation -> {
            Media m = invocation.getArgument(0);
            m.setId(new AggregateId(12345L, true));
            return null;
        }).when(mediaRepository).save(any(Media.class));

        // Act
        MediaDto result = handler.handle(command);

        // Assert
        assertNotNull(result);
        assertEquals("http://signed-url", result.getUrl());

        verify(objectStorageService).upload(eq(BucketType.PRIVATE), contains("image/"), any(), eq("image/jpeg"),
                eq(1024L), isNull());
        verify(mediaRepository).save(any(Media.class));

        verify(outboxRepository).save(argThat((ArgumentMatcher<Iterable<? extends DomainEvent>>) list -> {
            if (list instanceof List) {
                List<?> l = (List<?>) list;
                return !l.isEmpty() && l.get(0) instanceof MediaUploadedEvent;
            }
            return false;
        }), eq("Media"), anyString());
    }

    @Test
    void handle_shouldReuseExistingFile_whenMd5Exists() {
        // Arrange
        UploadMediaCommand command = new UploadMediaCommand(
                null,
                null,
                null,
                0L,
                "existing-md5",
                null,
                null,
                null,
                null);

        Media existingMedia = Media.builder()
                .id(new AggregateId(1L, true))
                .md5("existing-md5")
                .bucketType(BucketType.PRIVATE)
                .mediaType(MediaType.IMAGE)
                .objectKey("image/old.jpg")
                .build();

        when(mediaRepository.loadByMd5("existing-md5")).thenReturn(existingMedia);
        when(objectStorageService.getUrl(BucketType.PRIVATE, "image/old.jpg")).thenReturn("http://existing-url");

        // Act
        MediaDto result = handler.handle(command);

        // Assert
        assertEquals("http://existing-url", result.getUrl());
        verify(objectStorageService, never()).upload(any(), any(), any(), any(), anyLong(), any());
        verify(mediaRepository, never()).save(any());
    }
}
