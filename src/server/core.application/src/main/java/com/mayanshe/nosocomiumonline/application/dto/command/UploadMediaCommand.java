package com.mayanshe.nosocomiumonline.application.dto.command;

import com.mayanshe.nosocomiumonline.shared.cqrs.Command;
import lombok.Builder;
import lombok.Data;

import java.io.InputStream;

@Data
@Builder
public class UploadMediaCommand implements Command {
    private InputStream inputStream;
    private String fileName;
    private String contentType;
    private long sizeBytes;
    private String md5;
    private String bucketType; // "private" or "public"
    private String mediaType; // "image", "video", etc.
    private String title;
    private String description;
}
