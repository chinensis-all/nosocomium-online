package com.mayanshe.nosocomiumonline.infrastructure.persistence.repository;

import com.mayanshe.nosocomiumonline.domain.media.Media;
import com.mayanshe.nosocomiumonline.domain.media.MediaRepository;
import com.mayanshe.nosocomiumonline.domain.media.MediaType;
import com.mayanshe.nosocomiumonline.infrastructure.persistence.entity.MediaEntity;
import com.mayanshe.nosocomiumonline.infrastructure.persistence.mapper.MediaMapper;
import com.mayanshe.nosocomiumonline.shared.storage.BucketType;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MediaRepositoryImpl implements MediaRepository {

    private final MediaMapper mediaMapper;
    private final ModelMapper modelMapper; // Assuming ModelMapper is available

    @Override
    public Long save(Media media) {
        MediaEntity entity = toEntity(media);
        if (entity.getCreatedAt() == null) {
            entity.setCreatedAt(LocalDateTime.now());
        }
        entity.setUpdatedAt(LocalDateTime.now());
        mediaMapper.insert(entity);
        media.setId(entity.getId());
        return entity.getId();
    }

    @Override
    public void updateInfo(Media media) {
        MediaEntity entity = new MediaEntity();
        entity.setId(media.getId());
        entity.setTitle(media.getTitle());
        entity.setDescription(media.getDescription());
        entity.setUpdatedAt(LocalDateTime.now());
        mediaMapper.updateInfo(entity);
    }

    @Override
    public Optional<Media> findById(Long id) {
        MediaEntity entity = mediaMapper.selectById(id);
        return Optional.ofNullable(toDomain(entity));
    }

    @Override
    public Optional<Media> findByMd5(String md5) {
        MediaEntity entity = mediaMapper.selectByMd5(md5);
        return Optional.ofNullable(toDomain(entity));
    }

    @Override
    public void deleteById(Long id) {
        mediaMapper.deleteById(id);
    }

    private MediaEntity toEntity(Media media) {
        if (media == null)
            return null;
        return MediaEntity.builder()
                .id(media.getId())
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

    private Media toDomain(MediaEntity entity) {
        if (entity == null)
            return null;
        // Map back to Enum
        BucketType bucketType = "private".equals(entity.getBucketType()) ? BucketType.PRIVATE : BucketType.PUBLIC;
        MediaType mediaType;
        try {
            mediaType = MediaType.valueOf(entity.getMediaType().toUpperCase());
        } catch (Exception e) {
            // Fallback or handle appropriately
            mediaType = MediaType.OTHER;
        }

        return Media.builder()
                .id(entity.getId())
                .bucketType(bucketType)
                .mediaType(mediaType)
                .md5(entity.getMd5())
                .objectKey(entity.getObjectKey())
                .sizeBytes(entity.getSizeBytes())
                .contentType(entity.getContentType())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
