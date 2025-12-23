package com.mayanshe.nosocomiumonline.domain.media.value;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 媒体类型枚举。
 */
@Getter
@AllArgsConstructor
public enum MediaType {
    IMAGE("image", "图片"),
    AUDIO("audio", "音频"),
    VIDEO("video", "视频"),
    DOCUMENT("document", "文档"),
    ARCHIVE("archive", "压缩包"),
    OTHER("other", "其他");

    private final String code;
    private final String description;

    public static MediaType of(String code) {
        for (MediaType type : MediaType.values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown MediaType code: " + code);
    }
}
