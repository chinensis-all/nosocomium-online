package com.mayanshe.nosocomiumonline.doctor.controller;

import com.mayanshe.nosocomiumonline.application.dto.RegionDto;
import com.mayanshe.nosocomiumonline.application.dto.query.GetRegionListQuery;
import com.mayanshe.nosocomiumonline.shared.cqrs.QueryBus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * RegionController: 行政区划管理 Controller。
 *
 * @author zhangxihai
 */
@Tag(name = "行政区划管理", description = "获取全国行政区划信息")
@RestController
@RequestMapping("/regions")
@RequiredArgsConstructor
public class RegionController {
    private final QueryBus queryBus;

    @Operation(summary = "查询行政区划", description = "支持按父级ID过滤和关键词名称模糊搜索")
    @GetMapping
    public List<RegionDto> getRegionList(@RequestParam(value = "parentId", required = false, defaultValue = "0") Long parentId, @RequestParam(value = "keywords", required = false) String keywords) {
        parentId = parentId == null ? 0L : Math.max(0L, parentId);
        GetRegionListQuery query = new GetRegionListQuery(parentId, keywords);
        return queryBus.ask(query);
    }
}
