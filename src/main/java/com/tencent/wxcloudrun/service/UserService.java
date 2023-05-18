package com.tencent.wxcloudrun.service;

import com.tencent.wxcloudrun.model.UserMessage;

/**
 * @author liuyuzhuo
 * @date 2023/5/18 14:42
 */
public interface UserService {
    /**
     * 保存用户提交的信息 && 通知小程序管理员
     * @param userMessage 用户提交的信息
     * @return 1保存成功 0保存失败
     */
    int saveAndSendMessage(UserMessage userMessage);
}
