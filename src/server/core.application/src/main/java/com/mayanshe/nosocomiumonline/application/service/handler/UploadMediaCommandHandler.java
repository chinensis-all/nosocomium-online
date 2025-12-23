package com.mayanshe.nosocomiumonline.application.service.handler;

import cn.hutool.core.date.DateUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.mayanshe.nosocomiumonline.application.dto.MediaDto;
import com.mayanshe.nosocomiumonline.application.dto.command.UploadMediaCommand;
import com.mayanshe.nosocomiumonline.domain.kernel.eventing.OutboxRepository;
import com.mayanshe.nosocomiumonline.domain.media.model.Media;
import com.mayanshe.nosocomiumonline.domain.media.repository.MediaRepository;
import com.mayanshe.nosocomiumonline.domain.media.value.MediaType;
import com.mayanshe.nosocomiumonline.shared.contract.IdGenerator;
import com.mayanshe.nosocomiumonline.shared.cqrs.CommandHandler;
import com.mayanshe.nosocomiumonline.shared.event.DomainEventCollector;
import com.mayanshe.nosocomiumonline.shared.storage.BucketType;
import com.mayanshe.nosocomiumonline.shared.storage.ObjectStorageService;
import com.mayanshe.nosocomiumonline.shared.valueobject.AggregateId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

/**
 * UploadMediaCommandHandler: 处理上传媒体命令
 *
 * @author zhangxihai
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UploadMediaCommandHandler implements CommandHandler<UploadMediaCommand, MediaDto> {
    private final OutboxRepository outboxRepository;

    private final MediaRepository mediaRepository;

    private final ObjectStorageService objectStorageService;

    @Override
    public MediaDto handle(UploadMediaCommand command) {
        // 检查是否已存在相同 MD5 的媒体
        Media existingMedia = mediaRepository.loadByMd5(command.md5());
        if (existingMedia != null) {
            return toDto(existingMedia);
        }

        // 准备数据
        BucketType bucketType = BucketType.of(command.bucketType());
        MediaType mediaType = MediaType.of(command.mediaType());
        String extension = getExtension(command.fileName());
        StringBuilder path = new StringBuilder();
        path.append(mediaType.getCode())
                .append('/')
                .append(DateUtil.format(new Date(), "yyyyMM"))
                .append('/').append(UUID.randomUUID())
                .append(extension);

        // 上传到 COS、
        objectStorageService.upload(bucketType, path.toString(), command.inputStream(), command.contentType(),
                command.sizeBytes(), null);

        // 储存Media
        Media media = Media.builder()
                .id(new AggregateId(IdGenerator.nextId(), true))
                .bucketType(bucketType)
                .mediaType(mediaType)
                .md5(command.md5())
                .objectKey(path.toString())
                .sizeBytes(command.sizeBytes())
                .contentType(command.contentType())
                .title(command.title())
                .description(command.description())
                .build();
        mediaRepository.save(media);
        media.upload();

        // 记录Outbox事件
        outboxRepository.save(DomainEventCollector.drain(), Media.class.getSimpleName(),
                String.valueOf(media.getId().getId()));

        String url = objectStorageService.getUrl(media.getBucketType(), media.getObjectKey());
        return toDto(media, url);
    }

    private MediaDto toDto(Media media) {
        String url = objectStorageService.getUrl(media.getBucketType(), media.getObjectKey());
        return toDto(media, url);
    }

    private MediaDto toDto(Media media, String url) {

        return MediaDto.builder().id(media.getId().getId()).url(url).bucketType(media.getBucketType().getCode())
                .mediaType(media.getMediaType().getCode()).md5(media.getMd5()).sizeBytes(media.getSizeBytes())
                .contentType(media.getContentType()).title(media.getTitle()).description(media.getDescription())
                .createdAt(media.getCreatedAt()).build();
    }

    private String getExtension(String fileName) {
        if (fileName == null || fileName.lastIndexOf(".") == -1) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }
}
