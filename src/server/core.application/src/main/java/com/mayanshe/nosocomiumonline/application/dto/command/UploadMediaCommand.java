package com.mayanshe.nosocomiumonline.application.dto.command;

import com.mayanshe.nosocomiumonline.shared.cqrs.Command;

import java.util.Map;

/**
 * UploadMediaCommand: 上传媒体命令
 *
 * @author zhangxihai
 */
public record UploadMediaCommand(
        java.io.InputStream inputStream,
        String fileName,
        String contentType,
        long sizeBytes,
        String md5,
        String bucketType,
        String mediaType,
        String title,
        String description
) implements Command {
    public static final Map<String, String> BUCKET_TYPE_MAPPING = java.util.Map.of(
            "avatar", "private"
    );

    public void validate() {
        if (inputStream == null || md5 == null || md5.isEmpty() || bucketType == null || bucketType.isEmpty()) {
            throw new IllegalArgumentException("Invalid UploadMediaCommand parameters");
        }
    }

    @Override
    public String fileName() {
        return fileName == null ? "" : fileName;
    }

    @Override
    public String contentType() {
        return contentType == null ? "" : contentType;
    }

    @Override
    public String mediaType() {
        return mediaType == null ? "" : mediaType;
    }

    @Override
    public String title() {
        return title == null ? "" : title;
    }

    @Override
    public String description() {
        return description == null ? "" : description;
    }
}
