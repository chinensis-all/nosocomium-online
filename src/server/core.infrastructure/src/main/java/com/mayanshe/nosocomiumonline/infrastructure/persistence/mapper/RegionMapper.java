package com.mayanshe.nosocomiumonline.infrastructure.persistence.mapper;

import com.mayanshe.nosocomiumonline.application.dto.RegionDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 行政区划 Mapper 接口。
 *
 * @author zhangxihai
 */
@Mapper
public interface RegionMapper {
    List<RegionDto> search(@Param("parentId") Long parentId, @Param("keywords") String keywords);
}
