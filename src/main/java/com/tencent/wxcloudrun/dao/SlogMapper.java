package com.tencent.wxcloudrun.dao;

import com.tencent.wxcloudrun.model.Slog;
import com.tencent.wxcloudrun.model.Stype;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author liuyuzhuo
 * @date 2023/9/11 15:38
 */
@Mapper
public interface SlogMapper {

    int save(Slog slog);
}
