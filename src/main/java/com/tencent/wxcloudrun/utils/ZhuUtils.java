package com.tencent.wxcloudrun.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    /**
     * 获取时间字符串
     */
    public static String now(){
        LocalDateTime now = LocalDateTime.now();
        return now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss"));
    }
}
