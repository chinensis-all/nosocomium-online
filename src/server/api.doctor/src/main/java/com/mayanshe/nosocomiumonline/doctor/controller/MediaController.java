package com.mayanshe.nosocomiumonline.doctor.controller;

import cn.hutool.crypto.SecureUtil;
import com.mayanshe.nosocomiumonline.application.dto.MediaDto;
import com.mayanshe.nosocomiumonline.application.dto.MediaInfoDto;
import com.mayanshe.nosocomiumonline.application.dto.command.DeleteMediaCommand;
import com.mayanshe.nosocomiumonline.application.dto.command.ModifyMediaInfoCommand;
import com.mayanshe.nosocomiumonline.application.dto.command.UploadMediaCommand;
import com.mayanshe.nosocomiumonline.application.dto.query.GetMediaDetailQuery;
import com.mayanshe.nosocomiumonline.application.dto.query.GetMeidaPageQuery;
import com.mayanshe.nosocomiumonline.doctor.requests.ModifyMediaInfoRequest;
import com.mayanshe.nosocomiumonline.shared.cqrs.CommandBus;
import com.mayanshe.nosocomiumonline.shared.cqrs.QueryBus;
import com.mayanshe.nosocomiumonline.shared.exception.BadRequestException;
import com.mayanshe.nosocomiumonline.shared.valueobject.Pagination;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * 媒体控制器，处理文件上传、下载与管理。
 *
 * @author zhangxihai
 */
@Tag(name = "媒体资源管理", description = "文件上传、下载与管理")
@Slf4j
@RestController
@RequestMapping("/medias")
@RequiredArgsConstructor
public class MediaController {
        private final CommandBus commandBus;

        private final QueryBus queryBus;

        /**
         * 上传文件
         */
        @Operation(summary = "上传文件", description = "上传文件到指定的媒体类型（/medias/images, /medias/audios, /medias/videos, /medias/documents, /medias/archives）")
        @PostMapping("/{mediaType}")
        public MediaDto upload(
                @Parameter(description = "媒体类型", example = "images", required = true, schema = @Schema(allowableValues = {
                        "images", "audios", "videos", "documents",
                        "archives"})) @PathVariable("mediaType") String mediaType,
                @Parameter(description = "文件的业务分类 (可选)", example = "avatar") @RequestParam(value = "kind", required = false) String kind,
                @Parameter(description = "待上传的文件文件", required = true) @RequestParam("file") MultipartFile file)
                throws IOException {
                Set<String> allowedTypes = Set.of("images", "audios", "videos", "documents", "archives");

                if (!allowedTypes.contains(mediaType.toLowerCase())) {
                        throw new BadRequestException("不支持的文件类型: " + mediaType);
                }

                mediaType = mediaType.substring(0, mediaType.length() - 1);

                UploadMediaCommand command = new UploadMediaCommand(
                        file.getInputStream(),
                        file.getOriginalFilename(),
                        file.getContentType(),
                        file.getSize(),
                        SecureUtil.md5(file.getInputStream()),
                        UploadMediaCommand.BUCKET_TYPE_MAPPING.getOrDefault(mediaType, "private"),
                        mediaType,
                        file.getName(),
                        "");

                return commandBus.dispatch(command);
        }

        /**
         * 修改文件信息
         */
        @Operation(summary = "修改文件信息", description = "修改文件的标题和描述信息")
        @PutMapping("/{id:[1-9]\\d+}/info")
        public MediaInfoDto updateInfo(@PathVariable("id") Long id, @RequestBody ModifyMediaInfoRequest request) {
                ModifyMediaInfoCommand command = new ModifyMediaInfoCommand(
                        id,
                        request.getTitle(),
                        request.getDescription());
                return commandBus.dispatch(command);
        }

        /**
         * 删除文件
         */
        @Operation(summary = "删除文件", description = "根据文件ID删除文件")
        @DeleteMapping("/{id:[1-9]\\d+}")
        public void deleteMedia(@PathVariable("id") Long id) {
                DeleteMediaCommand command = new DeleteMediaCommand(id);
                commandBus.dispatch(command);
        }

        /**
         * 获取文件详情
         */
        @Operation(summary = "获取文件详情", description = "根据文件ID获取文件的详细信息")
        @GetMapping("/{id:[1-9]\\d+}")
        public MediaDto getMediaDetail(@PathVariable("id") Long id) {
                return queryBus.ask(new GetMediaDetailQuery(id));
        }

        @Operation(summary = "获取文件分页列表", description = "根据条件获取文件的分页列表")
        @GetMapping
        public Pagination<MediaDto> getMediaPage(
                @RequestParam(value = "bucketType", required = false) String bucketType,
                @RequestParam(value = "mediaType", required = false) String mediaType,
                @RequestParam(value = "keywords", required = false) String keywords,
                @RequestParam(value = "page", defaultValue = "0") int page,
                @RequestParam(value = "pageSize", defaultValue = "10") int size) {
                GetMeidaPageQuery query = new GetMeidaPageQuery(
                        bucketType,
                        mediaType,
                        keywords,
                        page,
                        size);
                return queryBus.ask(query);
        }
}
