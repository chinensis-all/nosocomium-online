package com.mayanshe.nosocomiumonline.boss.controller;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.core.io.IoUtil;
import com.mayanshe.nosocomiumonline.application.dto.MediaDto;
import com.mayanshe.nosocomiumonline.application.dto.command.DeleteMediaCommand;
import com.mayanshe.nosocomiumonline.application.dto.command.UploadMediaCommand;
import com.mayanshe.nosocomiumonline.application.dto.query.GetMediaDetailQuery;
import com.mayanshe.nosocomiumonline.shared.cqrs.CommandBus;
import com.mayanshe.nosocomiumonline.shared.cqrs.QueryBus;
import com.mayanshe.nosocomiumonline.shared.exception.BadRequestException;
import com.mayanshe.nosocomiumonline.shared.response.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/medias")
@RequiredArgsConstructor
@Tag(name = "媒体管理", description = "文件上传、下载与管理")
public class MediaController {

    private final CommandBus commandBus;
    private final QueryBus queryBus;

    /**
     * 上传文件
     *
     * @param mediaType 文件类型 (image, audio, video, document, etc.)
     * @param file      文件
     * @param bucket    存储桶类型 (private, public), 默认 private
     * @param title     标题 (可选)
     * @param desc      描述 (可选)
     */
    @PostMapping(value = "/{mediaType}", consumes = "multipart/form-data")
    @Operation(summary = "上传文件")
    public Result<MediaDto> upload(
            @Parameter(description = "文件类型 (image, audio, video, document)", required = true) @PathVariable String mediaType,
            @Parameter(description = "文件", required = true) @RequestParam("file") MultipartFile file,
            @Parameter(description = "存储桶类型 (private, public)") @RequestParam(value = "bucket", defaultValue = "private") String bucket,
            @Parameter(description = "标题") @RequestParam(value = "title", required = false) String title,
            @Parameter(description = "描述") @RequestParam(value = "desc", required = false) String desc)
            throws IOException {

        if (file.isEmpty()) {
            throw new BadRequestException("文件不能为空");
        }

        // 计算 MD5 (注意：对于大文件，这里可能会消耗内存，建议流式计算，但 MultipartFile 通常在内存或临时文件)
        // 这里使用 Hutool 的 SecureUtil 计算流 MD5
        String md5 = SecureUtil.md5(file.getInputStream());

        UploadMediaCommand command = UploadMediaCommand.builder()
                .inputStream(file.getInputStream())
                .fileName(file.getOriginalFilename())
                .contentType(file.getContentType())
                .sizeBytes(file.getSize())
                .md5(md5)
                .bucketType(bucket)
                .mediaType(mediaType)
                .title(title)
                .description(desc)
                .build();

        MediaDto mediaDto = commandBus.dispatch(command);
        return Result.success(mediaDto);
    }

    /**
     * 获取文件详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取文件详情")
    public Result<MediaDto> getDetail(@Parameter(description = "文件ID") @PathVariable Long id) {
        GetMediaDetailQuery query = GetMediaDetailQuery.builder().id(id).build();
        MediaDto mediaDto = queryBus.ask(query);
        return Result.success(mediaDto);
    }

    /**
     * 删除文件
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除文件")
    public Result<Void> delete(@Parameter(description = "文件ID") @PathVariable Long id) {
        DeleteMediaCommand command = DeleteMediaCommand.builder().id(id).build();
        commandBus.dispatch(command);
        return Result.success();
    }
}
