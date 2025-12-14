package com.mayanshe.nosocomiumonline.infrastructure.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface DemoMapper {
    @Select("SELECT 1")
    int testConnection();
}
