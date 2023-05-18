package com.tencent.wxcloudrun.service.impl;

import com.tencent.wxcloudrun.dao.UserMapper;
import com.tencent.wxcloudrun.model.UserMessage;
import com.tencent.wxcloudrun.service.UserService;
import com.tencent.wxcloudrun.utils.ThreadPoolUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author liuyuzhuo
 * @date 2023/5/18 14:42
 */
@Service
public class UserServiceImpl implements UserService {

    @Resource
    UserMapper userMapper;

    @Override
    public int saveAndSendMessage(final UserMessage userMessage) {
        int a = userMapper.saveMessage(userMessage);

        //异步通知小程序管理员
        ThreadPoolUtil.execute(() -> {
            System.out.println("通知管理员" + userMessage.toString());
        });

        return a;
    }
}
