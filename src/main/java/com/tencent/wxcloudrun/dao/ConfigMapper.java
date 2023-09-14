package com.tencent.wxcloudrun.dao;

import com.tencent.wxcloudrun.model.Config;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author liuyuzhuo
 * @date 2023/9/11 15:38
 */
@Mapper
public interface ConfigMapper {
    List<Config> listConfig(@Param("nameList") List<String> nameList);
}
