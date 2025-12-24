package com.mayanshe.nosocomiumonline.application.dto;

import com.mayanshe.nosocomiumonline.shared.contract.IdAccessor;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 行政区划 DTO。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "行政区划信息")
public class RegionDto implements IdAccessor {

    @Schema(description = "行政编码", example = "110000")
    private Long id;

    @Schema(description = "上级行政编码", example = "0")
    private Long parentId;

    @Schema(description = "行政区划级别 1:省 2:市 3:区/县 4:镇/街道 5:村/社区", example = "1")
    private Integer regionLevel;

    @Schema(description = "邮政编码", example = "100000")
    private String postalCode;

    @Schema(description = "区号", example = "010")
    private String areaCode;

    @Schema(description = "行政区划名称", example = "北京市")
    private String regionName;

    @Schema(description = "行政区划名称拼音", example = "bei jing shi")
    private String namePinyin;

    @Schema(description = "行政区划简称", example = "北京")
    private String shortName;

    @Schema(description = "行政区划组合名称", example = "中国,北京市")
    private String mergeName;

    @Schema(description = "经度", example = "116.405285")
    private BigDecimal longitude;

    @Schema(description = "纬度", example = "39.904989")
    private BigDecimal latitude;
}
