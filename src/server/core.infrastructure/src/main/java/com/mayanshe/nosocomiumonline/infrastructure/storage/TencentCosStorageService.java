package com.mayanshe.nosocomiumonline.infrastructure.storage;

import com.mayanshe.nosocomiumonline.shared.exception.InternalServerErrorException;
import com.mayanshe.nosocomiumonline.shared.storage.BucketType;
import com.mayanshe.nosocomiumonline.shared.storage.ObjectStorageService;
import com.mayanshe.nosocomiumonline.shared.util.PrintUtils;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.http.HttpMethodName;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.region.Region;
// import com.tencent.cloud.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.Map;

/**
 * 腾讯云 COS 存储服务实现。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TencentCosStorageService implements ObjectStorageService {

    private final CosConfig cosConfig;
    private final CosCredentialManager credentialManager;

    @Override
    public String upload(BucketType bucketType, String objectKey, InputStream inputStream, String contentType,
            long contentLength, Map<String, String> metadata) {
        COSClient cosClient = createCosClient();
        try {
            String bucketName = getBucketName(bucketType);
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType(contentType);
            objectMetadata.setContentLength(contentLength);

            if (metadata != null) {
                for (Map.Entry<String, String> entry : metadata.entrySet()) {
                    objectMetadata.addUserMetadata(entry.getKey(), entry.getValue());
                }
            }

            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectKey, inputStream,
                    objectMetadata);
            cosClient.putObject(putObjectRequest);

            return getUrl(bucketType, objectKey);
        } catch (Exception e) {
            log.error("Failed to upload file to COS. key={}", objectKey, e);
            throw new InternalServerErrorException("文件上传失败");
        } finally {
            cosClient.shutdown();
        }
    }

    @Override
    public void delete(BucketType bucketType, String objectKey) {
        COSClient cosClient = createCosClient();
        try {
            String bucketName = getBucketName(bucketType);
            cosClient.deleteObject(bucketName, objectKey);
        } catch (Exception e) {
            log.error("Failed to delete file from COS. key={}", objectKey, e);
            throw new InternalServerErrorException("文件删除失败");
        } finally {
            cosClient.shutdown();
        }
    }

    @Override
    public String getUrl(BucketType bucketType, String objectKey) {
        // 公有读桶：直接拼接 URL
        if (bucketType == BucketType.PUBLIC) {
            // 如果配置了 CDN 域名，优先使用 CDN
            if (StringUtils.hasText(cosConfig.getCdnDomain())) {
                return ensureProtocol(cosConfig.getCdnDomain()) + "/" + objectKey;
            }
            // 否则使用 COS 默认域名
            return "https://" + getBucketName(bucketType) + ".cos." + cosConfig.getRegion() + ".myqcloud.com/"
                    + objectKey;
        }

        // 私有桶：生成签名 URL
        COSClient cosClient = createCosClient();
        try {
            String bucketName = getBucketName(bucketType);
            // 签名有效期默认 1 小时 (3600秒)
            Date expiration = new Date(System.currentTimeMillis() + 3600 * 1000);
            URL url = cosClient.generatePresignedUrl(bucketName, objectKey, expiration, HttpMethodName.GET);

            String signedUrl = url.toString();

            // 如果配置了 CDN 域名，替换 Host 为 CDN 域名（CDN 鉴权回源）
            if (StringUtils.hasText(cosConfig.getCdnDomain())) {
                String cosHost = url.getHost();
                signedUrl = signedUrl.replace(cosHost, removeProtocol(cosConfig.getCdnDomain()));
            }

            return signedUrl;
        } finally {
            cosClient.shutdown();
        }
    }

    private COSClient createCosClient() {
        // Fallback to static credentials
        COSCredentials cred = new BasicCOSCredentials(
                cosConfig.getSecretId(),
                cosConfig.getSecretKey());
        ClientConfig clientConfig = new ClientConfig(new Region(cosConfig.getRegion()));
        return new COSClient(cred, clientConfig);
    }

    private String getBucketName(BucketType bucketType) {
        return bucketType == BucketType.PRIVATE ? cosConfig.getBucketPrivate() : cosConfig.getBucketPublic();
    }

    // 辅助方法：确保 URL 带协议头
    private String ensureProtocol(String domain) {
        if (!domain.startsWith("http://") && !domain.startsWith("https://")) {
            return "https://" + domain;
        }
        return domain;
    }

    // 辅助方法：移除协议头 (用于 Host 替换)
    private String removeProtocol(String domain) {
        return domain.replace("http://", "").replace("https://", "");
    }
}
