package com.tencent.wxcloudrun.utils;

import java.util.UUID;

/**
 * @author liuyuzhuo
 * @date 2023/5/18 18:36
 */
public class ZhuUtils {

    /**
     * 获取UUID，去掉-，转为大写
     */
    public static String uuid(){
        return UUID.randomUUID().toString().replace("-","").toUpperCase();
    }
}
