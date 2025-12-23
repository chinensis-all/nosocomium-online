package com.mayanshe.nosocomiumonline.shared.storage;

import java.io.InputStream;
import java.util.Map;

/**
 * ObjectStorageService: 对象存储服务接口。
 * 屏蔽具体云厂商（如腾讯云 COS）的实现细节。
 */
public interface ObjectStorageService {

    /**
     * 上传文件。
     *
     * @param bucketType    桶类型
     * @param objectKey     对象键（路径 + 文件名）
     * @param inputStream   文件流
     * @param contentType   文件内容类型
     * @param contentLength 文件长度
     * @param metadata      元数据（可选）
     * @return 文件的访问 URL（可能是签名 URL 或 CDN URL）
     */
    String upload(BucketType bucketType, String objectKey, InputStream inputStream, String contentType,
            long contentLength, Map<String, String> metadata);

    /**
     * 删除文件。
     *
     * @param bucketType 桶类型
     * @param objectKey  对象键
     */
    void delete(BucketType bucketType, String objectKey);

    /**
     * 获取文件访问 URL。
     * 对于私有桶，生成签名 URL；对于公有桶，生成普通 URL。
     *
     * @param bucketType 桶类型
     * @param objectKey  对象键
     * @return 访问 URL
     */
    String getUrl(BucketType bucketType, String objectKey);
}
