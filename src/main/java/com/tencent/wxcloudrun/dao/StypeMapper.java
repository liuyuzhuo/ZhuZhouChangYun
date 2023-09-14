package com.tencent.wxcloudrun.dao;

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
public interface StypeMapper {
    @Select("select id,type,name,sid,weigh from mk_stype order by weigh desc")
    List<Stype> listStype();

    @Select("select id,type,name,sid,weigh from mk_stype where id= #{id} order by weigh desc limit 1")
    Stype getStypeById(@Param("id") Long id);
}
