package com.mayanshe.nosocomiumonline.infrastructure.persistence.repository;

import com.mayanshe.nosocomiumonline.application.dto.MediaDto;
import com.mayanshe.nosocomiumonline.application.service.repository.MediaQueryRepository;
import com.mayanshe.nosocomiumonline.domain.media.model.Media;
import com.mayanshe.nosocomiumonline.domain.media.repository.MediaRepository;
import com.mayanshe.nosocomiumonline.domain.media.value.MediaType;
import com.mayanshe.nosocomiumonline.infrastructure.persistence.entity.MediaEntity;
import com.mayanshe.nosocomiumonline.infrastructure.persistence.mapper.MediaMapper;
import com.mayanshe.nosocomiumonline.infrastructure.util.Pager;
import com.mayanshe.nosocomiumonline.shared.exception.InternalServerErrorException;
import com.mayanshe.nosocomiumonline.shared.storage.BucketType;
import com.mayanshe.nosocomiumonline.shared.storage.ObjectStorageService;
import com.mayanshe.nosocomiumonline.shared.util.PrintUtils;
import com.mayanshe.nosocomiumonline.shared.valueobject.AggregateId;
import com.mayanshe.nosocomiumonline.shared.valueobject.Pagination;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;

/**
 * MediaRepositoryImpl: 媒体仓储实现
 *
 * @author zhangxihai
 */
@Repository
public class MediaRepositoryImpl implements MediaRepository, MediaQueryRepository {
    private final MediaMapper mapper;

    private final ObjectStorageService storageService;

    public MediaRepositoryImpl(MediaMapper mapper, @Lazy ObjectStorageService storageService) {
        this.mapper = mapper;
        this.storageService = storageService;
    }

    @Override
    public Media loadByMd5(String md5) {
        MediaEntity entity = mapper.selectByMd5(md5);
        if (entity == null) {
            return null;
        }

        return toAggregate(entity);
    }

    @Override
    public Optional<Media> load(Long id) {
        MediaEntity po = getEntity(id);
        if (po == null) {
            return Optional.empty();
        }

        return Optional.of(toAggregate(po));
    }

    @Override
    public void save(Media media) {
        MediaEntity entity = toEntity(media);

        if (media.getId().isNewed()) {
            if (mapper.insert(entity) <= 0) {
                throw new InternalServerErrorException("添加媒体资源错误");
            }
            media.getId().setId(entity.getId());
            media.getId().setNewed(false);
            return;
        }

        if (mapper.update(entity) <= 0) {
            throw new InternalServerErrorException("修改媒体资源信息错误");
        }
    }

    @Override
    public void destroy(Long id) {
        if (mapper.deleteById(id) <= 0) {
            throw new InternalServerErrorException("删除媒体资源信息错误");
        }
    }

    @Override
    public Optional<MediaDto> queryById(Long id) {
        MediaEntity entity = getEntity(id);
        if (entity == null) {
            return Optional.empty();
        }

        return Optional.of(toDto(entity));
    }

    private MediaEntity getEntity(Long id) {
        if (id == null || id <= 0) {
            return null;
        }

        return mapper.selectById(id);
    }

    @Override
    public Pagination<MediaDto> queryPage(Map<String, Object> criteria, int page, int size) {
        return Pager.paginate(mapper, criteria, this::toDto, page, size);
    }

    private Media toAggregate(MediaEntity entity) {
        return Media.builder()
                .id(AggregateId.of(entity.getId()))
                .bucketType(BucketType.of(entity.getBucketType()))
                .mediaType(MediaType.of(entity.getMediaType()))
                .md5(entity.getMd5())
                .objectKey(entity.getObjectKey())
                .sizeBytes(entity.getSizeBytes())
                .contentType(entity.getContentType())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .build();
    }

    private MediaEntity toEntity(Media media) {
        return MediaEntity.builder()
                .id(media.getId().getId())
                .bucketType(media.getBucketType().getCode())
                .mediaType(media.getMediaType().getCode())
                .md5(media.getMd5())
                .objectKey(media.getObjectKey())
                .sizeBytes(media.getSizeBytes())
                .contentType(media.getContentType())
                .title(media.getTitle())
                .description(media.getDescription())
                .createdAt(media.getCreatedAt())
                .updatedAt(media.getUpdatedAt())
                .build();
    }

    private MediaDto toDto(MediaEntity media) {
        return MediaDto.builder()
                .id(media.getId())
                .url(storageService.getUrl(BucketType.of(media.getBucketType()), media.getObjectKey()))
                .bucketType(media.getBucketType())
                .mediaType(media.getMediaType())
                .md5(media.getMd5())
                .sizeBytes(media.getSizeBytes())
                .contentType(media.getContentType())
                .title(media.getTitle())
                .description(media.getDescription())
                .createdAt(media.getCreatedAt())
                .updatedAt(media.getUpdatedAt())
                .build();
    }
}
