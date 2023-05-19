package com.tencent.wxcloudrun.config;

import com.tencent.wxcloudrun.service.UserService;
import com.tencent.wxcloudrun.utils.ThreadPoolUtil;
import com.tencent.wxcloudrun.utils.ZhuUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author liuyuzhuo
 * @date 2023/5/19 15:31
 * 数据库初始化，启动的时候执行。
 */
@Component
public class DataBasePrepare {
    @Resource
    private UserService userService;

    @PostConstruct
    public void dataBaseInit(){
        ThreadPoolUtil.execute(() -> {
            try {
                TimeUnit.SECONDS.sleep(3L);
                userService.initDataBase();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }
}
