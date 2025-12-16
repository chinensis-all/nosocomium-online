package com.mayanshe.nosocomiumonline.application.service.handler;

import com.mayanshe.nosocomiumonline.application.dto.MediaDto;
import com.mayanshe.nosocomiumonline.application.dto.command.UploadMediaCommand;
import com.mayanshe.nosocomiumonline.domain.media.Media;
import com.mayanshe.nosocomiumonline.domain.media.MediaRepository;
import com.mayanshe.nosocomiumonline.domain.media.MediaType;
import com.mayanshe.nosocomiumonline.shared.storage.BucketType;
import com.mayanshe.nosocomiumonline.shared.storage.ObjectStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.mayanshe.nosocomiumonline.shared.event.DomainEventPublisher;
import com.mayanshe.nosocomiumonline.domain.media.event.MediaUploadedEvent;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayInputStream;
import java.util.Optional;

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
    private DomainEventPublisher domainEventPublisher;

    @InjectMocks
    private UploadMediaCommandHandler handler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void handle_shouldUploadNewFile_whenMd5notExists() {
        // Arrange
        UploadMediaCommand command = UploadMediaCommand.builder()
                .md5("new-md5")
                .fileName("test.jpg")
                .contentType("image/jpeg")
                .sizeBytes(1024L)
                .bucketType("private")
                .mediaType("image")
                .inputStream(new ByteArrayInputStream(new byte[0]))
                .build();

        when(mediaRepository.findByMd5("new-md5")).thenReturn(Optional.empty());
        when(objectStorageService.getUrl(any(), any())).thenReturn("http://signed-url");

        // Act
        MediaDto result = handler.handle(command);

        // Assert
        assertNotNull(result);
        assertEquals("http://signed-url", result.getUrl());

        verify(objectStorageService).upload(eq(BucketType.PRIVATE), contains("image/"), any(), eq("image/jpeg"),
                eq(1024L), isNull());
        verify(mediaRepository).save(any(Media.class));
        verify(domainEventPublisher).publish(any(MediaUploadedEvent.class));
    }

    @Test
    void handle_shouldReuseExistingFile_whenMd5Exists() {
        // Arrange
        UploadMediaCommand command = UploadMediaCommand.builder()
                .md5("existing-md5")
                .build();

        Media existingMedia = Media.builder()
                .id(1L)
                .md5("existing-md5")
                .bucketType(BucketType.PRIVATE)
                .mediaType(MediaType.IMAGE)
                .objectKey("image/old.jpg")
                .build();

        when(mediaRepository.findByMd5("existing-md5")).thenReturn(Optional.of(existingMedia));
        when(objectStorageService.getUrl(BucketType.PRIVATE, "image/old.jpg")).thenReturn("http://existing-url");

        // Act
        MediaDto result = handler.handle(command);

        // Assert
        assertEquals("http://existing-url", result.getUrl());
        verify(objectStorageService, never()).upload(any(), any(), any(), any(), anyLong(), any());
        verify(mediaRepository, never()).save(any());
    }
}
