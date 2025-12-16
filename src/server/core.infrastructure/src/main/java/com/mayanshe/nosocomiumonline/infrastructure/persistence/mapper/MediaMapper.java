package com.mayanshe.nosocomiumonline.infrastructure.persistence.mapper;

import com.mayanshe.nosocomiumonline.infrastructure.persistence.entity.MediaEntity;
import org.apache.ibatis.annotations.Param;
import java.util.Optional;

public interface MediaMapper {
    int insert(MediaEntity entity);

    int updateInfo(MediaEntity entity);

    MediaEntity selectById(@Param("id") Long id);

    MediaEntity selectByMd5(@Param("md5") String md5);

    int deleteById(@Param("id") Long id);
}
