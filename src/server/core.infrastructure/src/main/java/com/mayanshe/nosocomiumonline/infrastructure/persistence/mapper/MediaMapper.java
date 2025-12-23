package com.mayanshe.nosocomiumonline.infrastructure.persistence.mapper;

import com.mayanshe.nosocomiumonline.infrastructure.persistence.entity.MediaEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Mapper
public interface MediaMapper extends PageMapper<MediaEntity> {
    int insert(MediaEntity entity);

    int update(MediaEntity entity);

    int deleteById(Long id);

    MediaEntity selectById(Long id);

    MediaEntity selectByMd5(String md5);

    boolean existsByMd5(String md5);
}
