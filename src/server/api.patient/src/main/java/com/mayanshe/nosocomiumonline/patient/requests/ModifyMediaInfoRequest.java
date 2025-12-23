package com.mayanshe.nosocomiumonline.patient.requests;

import com.mayanshe.nosocomiumonline.infrastructure.validator.NotBlankSize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 修改媒体信息请求。
 *
 * @author zhangxihai
 */
@Data
public class ModifyMediaInfoRequest {
    @Schema(description = "标题", example = "我的文件", required = false)
    @NotBlankSize(max = 100, message = "标题长度不能超过 {max} 个字符")
    private String title;

    @Schema(description = "描述", example = "这是我的文件的描述信息", required = false)
    @NotBlankSize(max = 500, message = "描述长度不能超过 {max} 个字符")
    private String description;
}
