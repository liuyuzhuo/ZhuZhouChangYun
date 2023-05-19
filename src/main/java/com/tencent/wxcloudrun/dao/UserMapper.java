package com.tencent.wxcloudrun.dao;

import com.tencent.wxcloudrun.model.UserMessage;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author liuyuzhuo
 * @date 2023/5/18 14:48
 */
@Mapper
public interface UserMapper {
    int saveMessage(UserMessage userMessage);

    void createTable_user_message();

    void createTable_Counters();
}
