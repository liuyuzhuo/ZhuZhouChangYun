package com.tencent.wxcloudrun.dao;

import com.tencent.wxcloudrun.model.Slog;
import com.tencent.wxcloudrun.model.User;
import com.tencent.wxcloudrun.model.UserMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author liuyuzhuo
 * @date 2023/5/18 14:48
 */
@Mapper
public interface UserMapper {


    User getUserByOpenId(@Param("openid") String openid);

    List<Slog> getSlogList( User user);

    int save(User user);
}
