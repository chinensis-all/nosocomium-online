package com.mayanshe.nosocomiumonline.application.service.handler;

import com.mayanshe.nosocomiumonline.application.dto.MediaDto;
import com.mayanshe.nosocomiumonline.application.dto.query.GetMediaDetailQuery;
import com.mayanshe.nosocomiumonline.domain.media.Media;
import com.mayanshe.nosocomiumonline.domain.media.MediaRepository;
import com.mayanshe.nosocomiumonline.shared.cqrs.QueryHandler;
import com.mayanshe.nosocomiumonline.shared.exception.NotFoundException;
import com.mayanshe.nosocomiumonline.shared.storage.ObjectStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetMediaDetailQueryHandler implements QueryHandler<GetMediaDetailQuery, MediaDto> {

    private final MediaRepository mediaRepository;
    private final ObjectStorageService objectStorageService;

    @Override
    public MediaDto handle(GetMediaDetailQuery query) {
        Media media = mediaRepository.findById(query.getId())
                .orElseThrow(() -> new NotFoundException("文件不存在"));

        String url = objectStorageService.getUrl(media.getBucketType(), media.getObjectKey());

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
}
