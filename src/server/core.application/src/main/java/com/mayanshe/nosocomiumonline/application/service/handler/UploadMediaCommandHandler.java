package com.mayanshe.nosocomiumonline.application.service.handler;

import com.mayanshe.nosocomiumonline.application.dto.MediaDto;
import com.mayanshe.nosocomiumonline.application.dto.command.UploadMediaCommand;
import com.mayanshe.nosocomiumonline.domain.media.Media;
import com.mayanshe.nosocomiumonline.domain.media.MediaRepository;
import com.mayanshe.nosocomiumonline.domain.media.MediaType;
import com.mayanshe.nosocomiumonline.shared.cqrs.CommandHandler;
import com.mayanshe.nosocomiumonline.shared.exception.BadRequestException;
import com.mayanshe.nosocomiumonline.shared.storage.BucketType;
import com.mayanshe.nosocomiumonline.shared.storage.ObjectStorageService;
import com.mayanshe.nosocomiumonline.shared.event.DomainEventPublisher;
import com.mayanshe.nosocomiumonline.domain.media.event.MediaUploadedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class UploadMediaCommandHandler implements CommandHandler<UploadMediaCommand, MediaDto> {

    private final MediaRepository mediaRepository;
    private final ObjectStorageService objectStorageService;
    private final DomainEventPublisher domainEventPublisher;

    @Override
    public MediaDto handle(UploadMediaCommand command) {
        // 1. MD5 去重
        Optional<Media> existingMedia = mediaRepository.findByMd5(command.getMd5());
        if (existingMedia.isPresent()) {
            log.info("Media hash collision (dedicated), reusing existing media. md5={}", command.getMd5());
            return toDto(existingMedia.get());
        }

        // 2. 准备数据
        BucketType bucketType = "public".equalsIgnoreCase(command.getBucketType()) ? BucketType.PUBLIC
                : BucketType.PRIVATE;
        MediaType mediaType;
        try {
            mediaType = MediaType.valueOf(command.getMediaType().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("不支持的媒体类型: " + command.getMediaType());
        }

        // 生成对象键：media_type/timestamp/uuid.ext
        String extension = getExtension(command.getFileName());
        String objectKey = String.format("%s/%d/%s%s",
                mediaType.getCode(),
                System.currentTimeMillis(),
                UUID.randomUUID().toString(),
                extension);

        // 3. 上传到 COS
        // 注意：这里不需要手动处理并发，COS SDK 内部处理，且我们是同步上传
        objectStorageService.upload(
                bucketType,
                objectKey,
                command.getInputStream(),
                command.getContentType(),
                command.getSizeBytes(),
                null // metadata
        );

        // 4. 保存到数据库
        Media media = Media.builder()
                .bucketType(bucketType)
                .mediaType(mediaType)
                .md5(command.getMd5())
                .objectKey(objectKey)
                .sizeBytes(command.getSizeBytes())
                .contentType(command.getContentType())
                .title(command.getTitle())
                .description(command.getDescription())
                .build();

        mediaRepository.save(media);

        // 5. 发布领域事件
        String url = objectStorageService.getUrl(media.getBucketType(), media.getObjectKey());
        MediaUploadedEvent event = new MediaUploadedEvent(
                media.getId(),
                media.getMd5(),
                media.getObjectKey(),
                media.getBucketType(),
                media.getMediaType(),
                url);
        domainEventPublisher.publish(event);

        return toDto(media, url);
    }

    private MediaDto toDto(Media media) {
        String url = objectStorageService.getUrl(media.getBucketType(), media.getObjectKey());
        return toDto(media, url);
    }

    private MediaDto toDto(Media media, String url) {

        return MediaDto.builder()
                .id(media.getId())
                .url(url)
                .bucketType(media.getBucketType().getCode())
                .mediaType(media.getMediaType().getCode())
                .md5(media.getMd5())
                .sizeBytes(media.getSizeBytes())
                .contentType(media.getContentType())
                .title(media.getTitle())
                .description(media.getDescription())
                .createdAt(media.getCreatedAt())
                .build();
    }

    private String getExtension(String fileName) {
        if (fileName == null || fileName.lastIndexOf(".") == -1) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }
}
