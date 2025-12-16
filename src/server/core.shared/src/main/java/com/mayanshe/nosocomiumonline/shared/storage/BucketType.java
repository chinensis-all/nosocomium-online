package com.mayanshe.nosocomiumonline.shared.storage;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 存储桶类型。
 * 决定了文件的访问权限和 URL 生成策略。
 */
@Getter
@AllArgsConstructor
public enum BucketType {
    /**
     * 私有读写。
     * 必须通过签名 URL 访问。
     */
    PRIVATE("private", true),

    /**
     * 公有读，私有写。
     * 可以直接通过 URL 访问。
     */
    PUBLIC("public", false);

    private final String code;
    private final boolean isPrivate;
}
